package com.zaneodell.fossdocs.models.view

import Document
import DocumentDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaneodell.fossdocs.DocumentEvent
import com.zaneodell.fossdocs.DocumentState
import com.zaneodell.fossdocs.SortType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentViewModel(private val dao: DocumentDao) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.LASTOPENED)
    private val _documents = _sortType.flatMapLatest { sortType ->
        when(sortType) {
            SortType.NAME -> dao.getAllByName()
            SortType.LASTOPENED -> dao.getAllByLastOpened()
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList<Document>())
    private val _state = MutableStateFlow(DocumentState())

    val state = combine(_state, _sortType, _documents) { state, sortType, documents ->
        state.copy(
            documents = documents,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DocumentState())

    fun onEvent(event: DocumentEvent){
        when(event){
            is DocumentEvent.DeleteDocument -> {
                viewModelScope.launch {
                    dao.delete(event.document)
                }
            }
            DocumentEvent.HideDialog -> {
                _state.update { it.copy(isAddingDocument = false) }
            }
            DocumentEvent.SaveDocument -> {
                val name = state.value.name
                val path = state.value.path
                val lastOpened = state.value.lastOpened

                if (name.isBlank() || path.isBlank()){
                    return
                }

                val document = Document(
                    name = name,
                    path = path,
                    lastOpened = lastOpened

                )
                viewModelScope.launch {
                    dao.insert(document)
                }
                _state.update { it.copy(
                    isAddingDocument = false,
                    name = "",
                    path = ""

                ) }
            }
            is DocumentEvent.SetLastOpened -> {
                _state.update { it.copy(
                    lastOpened = event.lastOpened
                ) }
            }
            is DocumentEvent.SetName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is DocumentEvent.SetPath -> {
                _state.update { it.copy(
                    path = event.path
                ) }
            }
            DocumentEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingDocument = true
                ) }
            }
            is DocumentEvent.SortDocuments -> {
                _sortType.value = event.sortType
            }
        }
    }
}