package com.zaneodell.fossdocs.models.data

import Document
import com.zaneodell.fossdocs.enums.SortType

data class DocumentState(
    val documents: List<Document> = emptyList(),
    var name: String = "",
    var path: String = "",
    var lastOpened: Long = 0,
    val isAddingDocument: Boolean = false,
    val sortType: SortType = SortType.LASTOPENED
)