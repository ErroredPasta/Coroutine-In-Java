package com.example.simpletodoapp.search_history.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simpletodoapp.search_history.data.local.SearchHistoryEntity.Companion.SEARCH_HISTORY_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM $SEARCH_HISTORY_TABLE_NAME")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSearchHistory(searchHistory: SearchHistoryEntity)
    @Delete
    suspend fun deleteSearchHistory(searchHistory: SearchHistoryEntity)
}