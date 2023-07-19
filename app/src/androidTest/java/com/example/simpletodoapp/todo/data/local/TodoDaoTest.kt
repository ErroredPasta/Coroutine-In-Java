package com.example.simpletodoapp.todo.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.simpletodoapp.core.TestCoroutineRule
import com.example.simpletodoapp.core.regexPatternForSearching
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class TodoDaoTest {
    private lateinit var sut: TodoDao
    private lateinit var database: TodoDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = TodoDatabase::class.java
        ).build()

        sut = database.todoDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertMultipleTodos_thenGetAllTodosSuccessfully() = runTest {
        // arrange
        val todos = (1..10).createTodoEntityFromRange(createNewTodo = true)

        // act
        for (todo in todos) {
            sut.insertTodo(todoDetailEntity = todo)
        }

        // assert
        lateinit var todosFromDatabase: List<TodoEntity>

        val job = launch {
            sut.getTodos().collect {
                todosFromDatabase = it
            }
        }
        advanceUntilIdle()

        // remove id of todos
        assertThat(todosFromDatabase.map { it.copy(id = 0L) }).isEqualTo(todos)
        job.cancel()
    }


    @Test
    fun deleteTodo_deletedTodoNotInDatabase() = runTest {
        // arrange
        val todos = (1..10).createTodoEntityFromRange(createNewTodo = false).onEach { todo ->
            sut.insertTodo(todoDetailEntity = todo)
        }

        // act
        val todoToDelete = todos[0]
        sut.deleteTodo(todo = todoToDelete)

        // assert
        lateinit var todosFromDatabase: List<TodoEntity>

        val job = launch {
            sut.getTodos().collect {
                todosFromDatabase = it
            }
        }
        advanceUntilIdle()

        assertThat(todos).contains(todoToDelete)
        assertThat(todosFromDatabase).doesNotContain(todoToDelete)
        job.cancel()
    }

    @Test
    fun getTodoDetail_whenAnEntityWithGivenIdExists_thenSuccessfullyGetTheDetail() = runTest {
        // arrange
        (1..10).createTodoEntityFromRange(createNewTodo = false).forEach { todo ->
            sut.insertTodo(todoDetailEntity = todo)
        }

        // act
        val id = 1L
        val todoDetailEntity = sut.getTodoDetail(id = id)

        // assert
        assertThat(todoDetailEntity).isNotNull()
        assertThat(todoDetailEntity!!.id).isEqualTo(id)
    }

    @Test
    fun getTodoDetail_whenNoEntityHasGivenId_thenReturnNull() = runTest {
        // arrange
        (1..10).createTodoEntityFromRange(createNewTodo = false).forEach { todo ->
            sut.insertTodo(todoDetailEntity = todo)
        }

        // act
        val id = 100L // no entity has 100L as id
        val todoDetail = sut.getTodoDetail(id = id)

        // assert
        assertThat(todoDetail).isNull()
    }

    @Test
    fun getTodosContainingKeyword() = runTest {
        // arrange
        // run twice to have 2 same todos
        repeat(times = 2) {
            (1..10).createTodoEntityFromRange(createNewTodo = true).forEach { todo ->
                sut.insertTodo(todoDetailEntity = todo)
            }
        }

        // act
        val keyword = "todo 1"
        lateinit var todosFromDatabase: List<TodoEntity>
        val job = launch {
            sut.getTodosContainingKeyword(keyword = keyword).collect {
                todosFromDatabase = it
            }
        }
        advanceUntilIdle()

        // assert
        println(todosFromDatabase)

        val regex = Regex(keyword.regexPatternForSearching, RegexOption.IGNORE_CASE)
        assertThat(todosFromDatabase.filterNot { it.todo.contains(regex) }).isEmpty()

        job.cancel()
    }

    // region helper functions =====================================================================
    private fun IntRange.createTodoEntityFromRange(
        createNewTodo: Boolean = false,
    ): List<TodoEntity> = map {
        TodoEntity(
            id = if (createNewTodo) 0L else it.toLong(),
            todo = "todo $it",
            description = "description $it"
        )
    }
    // endregion helper functions ==================================================================
}