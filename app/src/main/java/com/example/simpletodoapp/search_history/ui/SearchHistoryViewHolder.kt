package com.example.simpletodoapp.search_history.ui

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.simpletodoapp.databinding.ViewHolderSearchHistoryBinding

class SearchHistoryViewHolder(
    private val binding: ViewHolderSearchHistoryBinding
) : ViewHolder(binding.root) {
    fun bind(uiState: SearchHistoryUiState) {
        binding.uiState = uiState
    }
}

data class SearchHistoryUiState(
    val query: String,
    val onClick: () -> Unit,
    val onDeleteClick: () -> Unit
)