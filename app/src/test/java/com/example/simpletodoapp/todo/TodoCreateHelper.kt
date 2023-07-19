package com.example.simpletodoapp.todo

import com.example.simpletodoapp.todo.domain.TodoDetail

fun IntRange.createTodoDetailFromRange(createNewTodo: Boolean = false): List<TodoDetail> = map {
    TodoDetail(
        id = if (createNewTodo) 0L else it.toLong(),
        todo = "todo $it",
        description = "description $it"
    )
}