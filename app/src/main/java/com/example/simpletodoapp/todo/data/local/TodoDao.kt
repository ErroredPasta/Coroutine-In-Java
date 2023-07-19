package com.example.simpletodoapp.todo.data.local

import androidx.room.*
import com.example.simpletodoapp.todo.data.local.TodoEntity.Companion.ID_COLUMN_NAME
import com.example.simpletodoapp.todo.data.local.TodoEntity.Companion.TODO_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT $ID_COLUMN_NAME, * FROM $TODO_TABLE_NAME")
    fun getTodos(): Flow<List<TodoEntity>>

    @Query("SELECT $ID_COLUMN_NAME, * FROM $TODO_TABLE_NAME WHERE $ID_COLUMN_NAME = :id")
    suspend fun getTodoDetail(id: Long): TodoDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todoDetailEntity: TodoDetailEntity)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("SELECT $ID_COLUMN_NAME, * FROM $TODO_TABLE_NAME WHERE todo MATCH :keyword")
    fun getTodosContainingKeyword(keyword: String): Flow<List<TodoEntity>>
}