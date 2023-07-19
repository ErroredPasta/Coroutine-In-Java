package com.example.simpletodoapp.todo.data.repository

import com.example.simpletodoapp.todo.data.local.TodoDao
import com.example.simpletodoapp.todo.data.mapper.toTodoDetail
import com.example.simpletodoapp.todo.data.mapper.toTodoDetailEntity
import com.example.simpletodoapp.todo.data.mapper.toTodoEntity
import com.example.simpletodoapp.todo.data.mapper.toTodoList
import com.example.simpletodoapp.todo.domain.Todo
import com.example.simpletodoapp.todo.domain.TodoDetail
import com.example.simpletodoapp.todo.domain.TodoException
import com.example.simpletodoapp.todo.domain.TodoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val dao: TodoDao,
    private val dispatcher: CoroutineDispatcher,
) : TodoRepository {
    override fun getTodos(): Flow<List<Todo>> {
        return dao.getTodos().map { it.toTodoList() }
    }

    override suspend fun getTodoDetail(id: Long): TodoDetail = withContext(dispatcher) {
        return@withContext dao.getTodoDetail(id = id)?.toTodoDetail()
            ?: throw TodoException.NoTodoDetailFound(id = id)
    }

    override suspend fun insertTodo(todoDetail: TodoDetail) = withContext(dispatcher) {
        dao.insertTodo(todoDetailEntity = todoDetail.toTodoDetailEntity())
    }

    override suspend fun deleteTodo(todo: Todo) = withContext(dispatcher) {
        dao.deleteTodo(todo = todo.toTodoEntity())
    }

    override fun getTodosContainingKeyword(keyword: String): Flow<List<Todo>> {
        return dao.getTodosContainingKeyword(keyword = keyword).map { it.toTodoList() }
    }
}