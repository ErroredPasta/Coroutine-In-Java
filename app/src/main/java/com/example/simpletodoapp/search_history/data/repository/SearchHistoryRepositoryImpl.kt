package com.example.simpletodoapp.search_history.data.repository

import com.example.simpletodoapp.search_history.data.local.SearchHistoryDao
import com.example.simpletodoapp.search_history.data.mapper.toSearchHistoryEntity
import com.example.simpletodoapp.search_history.data.mapper.toSearchHistoryList
import com.example.simpletodoapp.search_history.domain.SearchHistory
import com.example.simpletodoapp.search_history.domain.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val dao: SearchHistoryDao
) : SearchHistoryRepository {
    override fun getSearchHistory(): Flow<List<SearchHistory>> {
        return dao.getSearchHistory().map { it.toSearchHistoryList() }
    }

    override suspend fun insertSearchHistory(searchHistory: SearchHistory) {
        dao.insertSearchHistory(searchHistory = searchHistory.toSearchHistoryEntity())
    }

    override suspend fun deleteSearchHistory(searchHistory: SearchHistory) {
        dao.deleteSearchHistory(searchHistory = searchHistory.toSearchHistoryEntity())
    }
}