package com.example.simpletodoapp.search_history.ui.mapper

import com.example.simpletodoapp.search_history.domain.SearchHistory
import com.example.simpletodoapp.search_history.ui.SearchHistoryUiState

internal fun SearchHistory.toSearchHistoryUiState(
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) = SearchHistoryUiState(
    query = this,
    onClick = onClick,
    onDeleteClick = onDeleteClick
)