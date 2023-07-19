package com.example.simpletodoapp.todo.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.simpletodoapp.databinding.ViewHolderTodoBinding

class TodoAdapter : ListAdapter<TodoUiState, TodoViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            ViewHolderTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(uiState = getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<TodoUiState>() {
            override fun areItemsTheSame(oldItem: TodoUiState, newItem: TodoUiState): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TodoUiState, newItem: TodoUiState): Boolean {
                return oldItem == newItem
            }
        }
    }
}

