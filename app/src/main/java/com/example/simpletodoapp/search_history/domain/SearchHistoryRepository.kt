package com.example.simpletodoapp.search_history.domain

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getSearchHistory(): Flow<List<SearchHistory>>
    suspend fun insertSearchHistory(searchHistory: SearchHistory)
    suspend fun deleteSearchHistory(searchHistory: SearchHistory)
}