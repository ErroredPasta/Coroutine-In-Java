package com.example.simpletodoapp.todo.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.example.simpletodoapp.core.TestCoroutineRule
import com.example.simpletodoapp.todo.createTodoDetailFromRange
import com.example.simpletodoapp.todo.data.FakeTodoRepository
import com.example.simpletodoapp.todo.domain.TodoException
import com.example.simpletodoapp.todo.domain.TodoRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class TodoDetailViewModelTest {
    private lateinit var sut: TodoDetailViewModel
    private lateinit var repository: TodoRepository
    private lateinit var savedStateHandle: SavedStateHandle

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val initialTodoList = (1..10).createTodoDetailFromRange(createNewTodo = false)
    private val todoIdKey = "todo_id"

    @Before
    fun setup() {
        repository = FakeTodoRepository(initialList = initialTodoList)
        savedStateHandle = SavedStateHandle(mapOf(todoIdKey to 1L))
        sut = TodoDetailViewModel(
            repository = repository,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `if no todo id is given, IllegalArgumentException should be thrown`() {
        // act
        val result = runCatching {
            TodoDetailViewModel(
                repository = repository,
                savedStateHandle = SavedStateHandle(/* empty initial state */)
            )
        }

        // assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `if a valid todo id is given, then no exception should be thrown when creating a view model`() {
        // act
        val result = runCatching {
            TodoDetailViewModel(
                repository = repository,
                savedStateHandle = savedStateHandle
            )
        }

        // assert
        assertThat(result.isSuccess).isTrue()
    }

    @Test(timeout = 1_000L)
    fun `when collecting state from the view model, loading should be collected`() = runTest {
        // act
        val loadingChannel = Channel<Unit>()
        val job = launch {
            sut.state.collect {
                if (it == TodoDetailState.Loading) loadingChannel.send(Unit)
            }
        }

        // assert
        loadingChannel.receive()
        job.cancel()
    }

    @Test(timeout = 1_000L)
    fun `after the first subscription, the view model gets a todo detail with given id from the repository`() =
        runTest {
            // act
            val successChannel = Channel<TodoDetailState.Success>()
            val job = launch {
                sut.state.collect {
                    if (it is TodoDetailState.Success) successChannel.send(it)
                }
            }

            // assert
            val todoDetailSuccess = successChannel.receive()
            assertThat(todoDetailSuccess.todoDetail.id).isEqualTo(1L)
            job.cancel()
        }

    @Test(timeout = 1_000L)
    fun `the second subscription collect the same data as the first`() = runTest {
        // arrange
        val firstChannel = Channel<TodoDetailState.Success>()
        val firstJob = launch {
            sut.state.collect {
                if (it is TodoDetailState.Success) firstChannel.send(it)
            }
        }

        val firstTodoDetailSuccess = firstChannel.receive()

        // act
        lateinit var secondTodoDetailSuccess: TodoDetailState.Success
        val secondJob = launch {
            sut.state.collect {
                if (it is TodoDetailState.Success) secondTodoDetailSuccess = it
            }
        }
        advanceUntilIdle()

        // assert
        assertThat(secondTodoDetailSuccess.todoDetail.id).isEqualTo(1L)
        assertThat(firstTodoDetailSuccess.todoDetail).isEqualTo(secondTodoDetailSuccess.todoDetail)
        firstJob.cancel()
        secondJob.cancel()
    }

    @Test(timeout = 1_000L)
    fun `when no todo detail has the given id, error state should contain NoTodoDetailFound exception`() = runTest {
        // arrange
        sut = TodoDetailViewModel(
            repository = repository,
            // no TodoDetail has 100 as id
            savedStateHandle = SavedStateHandle(mapOf(todoIdKey to 100L))
        )

        // act
        val errorChannel = Channel<TodoDetailState.Error>()
        val job = launch {
            sut.state.collect {
                if (it is TodoDetailState.Error) errorChannel.send(it)
            }
        }

        // assert
        val error = errorChannel.receive()
        assertThat(error.cause).isInstanceOf(TodoException.NoTodoDetailFound::class.java)
        System.err.println(error.cause.message)
        job.cancel()
    }
}