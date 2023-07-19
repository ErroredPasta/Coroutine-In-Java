package com.example.simpletodoapp.todo.domain

data class Todo(
    val id: Long = 0L,
    val todo: String,
    val description: String? = null
)
