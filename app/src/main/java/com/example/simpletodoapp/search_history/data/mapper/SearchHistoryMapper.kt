package com.example.simpletodoapp.search_history.data.mapper

import com.example.simpletodoapp.search_history.data.local.SearchHistoryEntity
import com.example.simpletodoapp.search_history.domain.SearchHistory

internal fun SearchHistory.toSearchHistoryEntity() = SearchHistoryEntity(
    query = this
)

internal fun SearchHistoryEntity.toSearchHistory() = query

internal fun List<SearchHistoryEntity>.toSearchHistoryList() = map {
    it.toSearchHistory()
}