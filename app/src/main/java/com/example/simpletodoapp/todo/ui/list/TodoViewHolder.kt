package com.example.simpletodoapp.todo.ui.list

import androidx.recyclerview.widget.RecyclerView
import com.example.simpletodoapp.databinding.ViewHolderTodoBinding

class TodoViewHolder(
    private val binding: ViewHolderTodoBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(uiState: TodoUiState) {
        binding.uiState = uiState
    }
}

data class TodoUiState(
    val id: Long,
    val todo: String,
    val description: String? = null,
    val onClick: () -> Unit
)