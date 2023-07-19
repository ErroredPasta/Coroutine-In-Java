package com.example.simpletodoapp.todo.domain

import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(): Flow<List<Todo>>

    @Throws(TodoException.NoTodoDetailFound::class)
    suspend fun getTodoDetail(id: Long): TodoDetail

    suspend fun insertTodo(todoDetail: TodoDetail)
    suspend fun deleteTodo(todo: Todo)

    fun getTodosContainingKeyword(keyword: String): Flow<List<Todo>>
}