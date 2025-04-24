package com.zaneodell.fossdocs

import Document

data class DocumentState(
    val documents: List<Document> = emptyList<Document>(),
    var name: String = "",
    var path: String = "",
    var lastOpened: Long = 0,
    val isAddingDocument: Boolean = false,
    val sortType: SortType = SortType.LASTOPENED
)
