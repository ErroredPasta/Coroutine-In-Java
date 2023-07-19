package com.example.simpletodoapp.todo.data.mapper

import com.example.simpletodoapp.todo.data.local.TodoDetailEntity
import com.example.simpletodoapp.todo.data.local.TodoEntity
import com.example.simpletodoapp.todo.domain.Todo
import com.example.simpletodoapp.todo.domain.TodoDetail

internal fun TodoEntity.toTodo() = Todo(
    id = id,
    todo = todo,
    description = description
)

internal fun Todo.toTodoEntity() = TodoEntity(
    id = id,
    todo = todo,
    description = description
)

internal fun TodoDetailEntity.toTodoDetail() = TodoDetail(
    id = id,
    todo = todo,
    description = description
)

internal fun TodoDetail.toTodoDetailEntity() = TodoDetailEntity(
    id = id,
    todo = todo,
    description = description
)

internal fun List<TodoEntity>.toTodoList() = map { todo -> todo.toTodo() }