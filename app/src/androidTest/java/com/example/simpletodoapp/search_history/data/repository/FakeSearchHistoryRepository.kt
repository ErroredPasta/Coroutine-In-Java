package com.example.simpletodoapp.search_history.data.repository

import com.example.simpletodoapp.search_history.domain.SearchHistory
import com.example.simpletodoapp.search_history.domain.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class FakeSearchHistoryRepository(
    initialList: List<SearchHistory> = emptyList()
) : SearchHistoryRepository {
    private val searchHistoryListFlow = MutableStateFlow(initialList.toSet())

    override fun getSearchHistory(): Flow<List<SearchHistory>> {
        return searchHistoryListFlow.map { it.toList() }
    }

    override suspend fun insertSearchHistory(searchHistory: SearchHistory) {
        searchHistoryListFlow.update { it + searchHistory }
    }

    override suspend fun deleteSearchHistory(searchHistory: SearchHistory) {
        searchHistoryListFlow.update { it - searchHistory }
    }
}