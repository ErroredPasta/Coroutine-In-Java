package com.example.simpletodoapp.todo.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodoapp.todo.domain.TodoDetail
import com.example.simpletodoapp.todo.domain.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val repository: TodoRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val todoId: Long = requireNotNull(savedStateHandle["todo_id"]) {
        "todoId in TodoDetailViewModel is null"
    }

    private val _state = MutableStateFlow<TodoDetailState>(TodoDetailState.Loading)
    val state = _state.onSubscription {
        if (_state.value == TodoDetailState.Loading) {
            getTodoDetail(id = todoId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
        initialValue = TodoDetailState.Loading
    )

    private val todoDetailExceptionHandler = CoroutineExceptionHandler { _, cause ->
        _state.update { TodoDetailState.Error(cause = cause) }
    }

    private fun getTodoDetail(id: Long) {
        viewModelScope.launch(todoDetailExceptionHandler) {
            _state.update {
                TodoDetailState.Success(
                    todoDetail = repository.getTodoDetail(id = id)
                )
            }
        }
    }
}

sealed interface TodoDetailState {
    object Loading : TodoDetailState
    data class Success(val todoDetail: TodoDetail) : TodoDetailState
    data class Error(val cause: Throwable) : TodoDetailState
}