package com.example.simpletodoapp.todo.ui.insert

import com.example.simpletodoapp.core.TestCoroutineRule
import com.example.simpletodoapp.todo.createTodoDetailFromRange
import com.example.simpletodoapp.todo.data.FakeTodoRepository
import com.example.simpletodoapp.todo.domain.Todo
import com.example.simpletodoapp.todo.domain.TodoDetail
import com.example.simpletodoapp.todo.domain.TodoRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean


@OptIn(ExperimentalCoroutinesApi::class)
class TodoInsertViewModelTest {
    private lateinit var sut: TodoInsertViewModel
    private lateinit var repository: TodoRepository

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val initialTodoList = (1..10).createTodoDetailFromRange(createNewTodo = false)

    @Before
    fun setup() {
        repository = FakeTodoRepository(initialList = initialTodoList)
        sut = TodoInsertViewModel(repository = repository)
    }

    @Test
    fun `insert todo, the added one is in todos from repository`() = runTest {
        // act
        val todoToAdd = TodoDetail(
            // not assigned id as it should be assigned by database
            todo = "new todo to add"
        )
        sut.insertTodo(
            todoDetail = todoToAdd,
            onSuccess = ::doNothing,
            onError = ::doNothing
        )
        advanceUntilIdle()

        // assert
        lateinit var collectedTodos: List<Todo>

        val job = launch {
            repository.getTodos().collect {
                collectedTodos = it
            }
        }
        advanceUntilIdle()

        // remove id of collected todos
        assertThat(collectedTodos.map { it.copy(id = 0L) }).contains(todoToAdd.toTodo())
        job.cancel()
    }

    @Test
    fun `on success of inserting a todo, onSuccess must be invoked`() = runTest {
        // arrange
        val onSuccessInvoked = AtomicBoolean(false)
        // act
        sut.insertTodo(
            todoDetail = TodoDetail(
                // not assigned id as it should be assigned by database
                todo = "new todo to add"
            ),
            onSuccess = { onSuccessInvoked.set(true) },
            onError = ::doNothing
        )
        advanceUntilIdle()

        // assert
        assertThat(onSuccessInvoked.get()).isEqualTo(true)
    }

    @Test
    fun `no blank todo must be inserted`() = runTest {
        // arrange
        val blankTodoDetail = TodoDetail(
            todo = " " // blank todo
        )

        // act
        sut.insertTodo(
            todoDetail = blankTodoDetail,
            onSuccess = ::doNothing,
            onError = ::doNothing
        )
        advanceUntilIdle()

        // assert
        lateinit var collectedTodos: List<Todo>

        val job = launch {
            repository.getTodos().collect {
                collectedTodos = it
            }
        }
        advanceUntilIdle()

        assertThat(collectedTodos.filter { it.todo.isBlank() }).isEmpty()
        job.cancel()
    }

    @Test
    fun `when inserting a blank todo, onError must be invoked`() {
        // arrange
        val onErrorInvoked = AtomicBoolean(false)

        // act
        sut.insertTodo(
            todoDetail = TodoDetail(
                todo = " " // blank todo
            ),
            onSuccess = ::doNothing,
            onError = { onErrorInvoked.set(true) }
        )

        // assert
        assertThat(onErrorInvoked.get()).isTrue()
    }

    // region helper function ======================================================================
    // as TodoDetail may change, defined a function to convert a TodoDetail to the Todo type
    private fun TodoDetail.toTodo() = this

    private fun doNothing(): Unit = Unit
    // endregion helper function ===================================================================
}