package com.example.simpletodoapp.todo.ui.insert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodoapp.todo.domain.TodoDetail
import com.example.simpletodoapp.todo.domain.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoInsertViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    fun insertTodo(
        todoDetail: TodoDetail,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        if (todoDetail.todo.isBlank()) {
            onError()
            return
        }

        viewModelScope.launch {
            repository.insertTodo(todoDetail = todoDetail)
            onSuccess()
        }
    }
}