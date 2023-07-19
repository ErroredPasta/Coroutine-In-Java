package com.example.simpletodoapp.todo.data.repository

import com.example.simpletodoapp.core.regexPatternForSearching
import com.example.simpletodoapp.todo.domain.Todo
import com.example.simpletodoapp.todo.domain.TodoDetail
import com.example.simpletodoapp.todo.domain.TodoException
import com.example.simpletodoapp.todo.domain.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class FakeTodoRepository(
    initialList: List<TodoDetail> = emptyList(),
) : TodoRepository {
    private val list = MutableStateFlow(initialList)

    override fun getTodos(): Flow<List<Todo>> {
        return list
    }

    override suspend fun getTodoDetail(id: Long): TodoDetail {
        return list.value.find { it.id == id }
            ?: throw TodoException.NoTodoDetailFound(id = id)
    }

    override suspend fun insertTodo(todoDetail: TodoDetail) {
        val todoToInsert = if (todoDetail.id == 0L) {
            todoDetail.copy(id = list.value.size.toLong() + 1)
        } else {
            todoDetail
        }

        list.update { it + todoToInsert }
    }

    override suspend fun deleteTodo(todo: Todo) {
        list.update { it - todo }
    }

    override fun getTodosContainingKeyword(keyword: String): Flow<List<Todo>> {
        val regex = Regex(keyword.regexPatternForSearching, RegexOption.IGNORE_CASE)

        return flow {
            list.collect { todoDetails ->
                emit(todoDetails.filter { todoDetail -> todoDetail.todo.contains(regex = regex) })
            }
        }
    }
}