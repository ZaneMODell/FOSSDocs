package com.zaneodell.fossdocs.events

import Document
import com.zaneodell.fossdocs.enums.SortType

sealed interface DocumentEvent {

    object SaveDocument: DocumentEvent

    data class SetLastOpened(val lastOpened: Long): DocumentEvent

    data class SetName(val name: String): DocumentEvent

    data class SetPath(val path: String): DocumentEvent

    object ShowDialog: DocumentEvent
    object HideDialog: DocumentEvent
    data class SortDocuments(val sortType: SortType): DocumentEvent
    data class DeleteDocument(val document: Document): DocumentEvent
}