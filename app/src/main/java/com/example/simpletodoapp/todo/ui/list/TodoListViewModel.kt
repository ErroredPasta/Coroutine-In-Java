package com.example.simpletodoapp.todo.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodoapp.search_history.domain.SearchHistory
import com.example.simpletodoapp.search_history.domain.SearchHistoryRepository
import com.example.simpletodoapp.todo.domain.Todo
import com.example.simpletodoapp.todo.domain.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {
    private val searchKeyword = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val todos = searchKeyword.flatMapLatest { keyword ->
        if (keyword.isBlank()) {
            todoRepository.getTodos()
        } else {
            todoRepository.getTodosContainingKeyword(keyword = keyword)
        }
    }

    val searchHistories = searchHistoryRepository.getSearchHistory()

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo = todo)
        }
    }

    fun setSearchKeyword(keyword: String) {
        viewModelScope.launch {
            searchKeyword.update { keyword }
            searchHistoryRepository.insertSearchHistory(searchHistory = keyword)
        }
    }

    fun deleteSearchHistory(searchHistory: SearchHistory) {
        viewModelScope.launch {
            searchHistoryRepository.deleteSearchHistory(searchHistory = searchHistory)
        }
    }
}