package com.example.simpletodoapp.search_history.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.simpletodoapp.search_history.data.local.SearchHistoryEntity.Companion.SEARCH_HISTORY_TABLE_NAME

@Entity(tableName = SEARCH_HISTORY_TABLE_NAME)
data class SearchHistoryEntity(
    @PrimaryKey
    val query: String
) {
    companion object {
        const val SEARCH_HISTORY_TABLE_NAME = "search_history_table"
    }
}
