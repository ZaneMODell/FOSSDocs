package com.zaneodell.fossdocs.models.data

import Document
import com.zaneodell.fossdocs.enums.SortType

/**
 * Class representing the state of the document
 */
//TODO MAKE SURE YOU UNDERSTAND THIS IN RELATION TO THE VIEWMODEL
data class DocumentState(
    val documents: List<Document> = emptyList(),
    var name: String = "",
    var path: String = "",
    var lastOpened: Long = 0,
    val isAddingDocument: Boolean = false,
    val sortType: SortType = SortType.LASTOPENED
)