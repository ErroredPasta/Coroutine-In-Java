package com.example.simpletodoapp.todo.ui.mapper

import com.example.simpletodoapp.todo.domain.Todo
import com.example.simpletodoapp.todo.ui.list.TodoUiState

internal fun Todo.toTodoUiState(
    onClick: () -> Unit
) = TodoUiState(
    id = id,
    todo = todo,
    description = description,
    onClick = onClick
)

internal fun TodoUiState.toTodo() = Todo(
    id = id,
    todo = todo,
    description = description
)