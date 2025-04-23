package com.zaneodell.fossdocs

import Document

data class DocumentState(
    val documents: List<Document> = emptyList<Document>(),
    val name: String = "",
    val path: String = "",
    val lastOpened: Long = 0,
    val isAddingDocument: Boolean = false,
    val sortType: SortType = SortType.LASTOPENED
)
