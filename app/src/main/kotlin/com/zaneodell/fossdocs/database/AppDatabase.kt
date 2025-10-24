package com.zaneodell.fossdocs.database

import Document
import DocumentDao
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Document::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
}