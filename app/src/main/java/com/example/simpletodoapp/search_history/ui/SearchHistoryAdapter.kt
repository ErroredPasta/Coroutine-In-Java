package com.example.simpletodoapp.search_history.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.simpletodoapp.databinding.ViewHolderSearchHistoryBinding

class SearchHistoryAdapter : ListAdapter<SearchHistoryUiState, SearchHistoryViewHolder>(DiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        return SearchHistoryViewHolder(
            binding = ViewHolderSearchHistoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DiffUtil = object : DiffUtil.ItemCallback<SearchHistoryUiState>() {
            override fun areItemsTheSame(
                oldItem: SearchHistoryUiState,
                newItem: SearchHistoryUiState,
            ): Boolean {
                return oldItem.query == newItem.query
            }

            override fun areContentsTheSame(
                oldItem: SearchHistoryUiState,
                newItem: SearchHistoryUiState,
            ): Boolean {
                return oldItem.query == newItem.query
            }
        }
    }
}