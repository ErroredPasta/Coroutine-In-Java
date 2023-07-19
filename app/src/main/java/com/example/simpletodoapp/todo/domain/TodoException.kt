package com.example.simpletodoapp.todo.domain

sealed class TodoException(override val message: String? = null) : RuntimeException() {
    class NoTodoDetailFound(id: Long) : TodoException(
        message = "There is no TodoDetail whose id is $id"
    )
}