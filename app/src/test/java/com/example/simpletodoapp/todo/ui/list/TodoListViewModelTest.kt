package com.example.simpletodoapp.todo.ui.list

import com.example.simpletodoapp.core.TestCoroutineRule
import com.example.simpletodoapp.core.regexPatternForSearching
import com.example.simpletodoapp.search.data.FakeSearchHistoryRepository
import com.example.simpletodoapp.search_history.domain.SearchHistory
import com.example.simpletodoapp.search_history.domain.SearchHistoryRepository
import com.example.simpletodoapp.todo.createTodoDetailFromRange
import com.example.simpletodoapp.todo.data.FakeTodoRepository
import com.example.simpletodoapp.todo.domain.Todo
import com.example.simpletodoapp.todo.domain.TodoRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class TodoListViewModelTest {
    private lateinit var sut: TodoListViewModel
    private lateinit var todoRepository: TodoRepository
    private lateinit var searchHistoryRepository: SearchHistoryRepository

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val initialTodoList = (1..10).createTodoDetailFromRange(createNewTodo = false)
    private val initialSearchHistoryList = (1..10).map { "Search history $it" }

    @Before
    fun setup() {
        todoRepository = FakeTodoRepository(initialList = initialTodoList)
        searchHistoryRepository = FakeSearchHistoryRepository(initialList = initialSearchHistoryList)


        sut = TodoListViewModel(
            todoRepository = todoRepository,
            searchHistoryRepository = searchHistoryRepository
        )
    }

    @Test
    fun `collect all todos without any manipulation, it matches the initial list`() =
        runTest {
            // act
            lateinit var collectedTodos: List<Todo>

            val job = launch {
                sut.todos.collect {
                    collectedTodos = it
                }
            }
            advanceUntilIdle()

            // assert
            assertThat(collectedTodos).isEqualTo(initialTodoList)
            job.cancel()
        }

    @Test
    fun `delete todo, deleted todo is not in collected todos`() = runTest {
        // act
        val todoToDelete = initialTodoList[0]
        sut.deleteTodo(todo = todoToDelete)

        // assert
        lateinit var collectedTodos: List<Todo>

        val job = launch {
            sut.todos.collect {
                collectedTodos = it
            }
        }
        advanceUntilIdle()

        assertThat(collectedTodos).isEqualTo(initialTodoList - todoToDelete)
        job.cancel()
    }

    @Test
    fun `set keyword to search, only todos containing the keyword should be collected`() = runTest {
        // arrange
        (1..10).createTodoDetailFromRange(createNewTodo = true).forEach { todoDetail ->
            todoRepository.insertTodo(todoDetail = todoDetail)
        }

        // act
        val keyword = "todo 1"
        sut.setSearchKeyword(keyword)

        // assert
        lateinit var collectedTodos: List<Todo>
        val job = launch {
            sut.todos.collect {
                collectedTodos = it
            }
        }
        advanceUntilIdle()

        println(collectedTodos)

        val regex = Regex(keyword.regexPatternForSearching, RegexOption.IGNORE_CASE)
        assertThat(collectedTodos.filterNot { it.todo.contains(regex) }).isEmpty()

        job.cancel()
    }

    @Test
    fun `set keyword to search, insert the keyword to search history repository`() = runTest {
        // act
        val newKeyword = "New keyword"
        sut.setSearchKeyword(keyword = newKeyword)

        // assert
        lateinit var searchHistories: List<SearchHistory>
        val job = launch {
            sut.searchHistories.collect {
                searchHistories = it
            }
        }
        advanceUntilIdle()

        assertThat(searchHistories).contains(newKeyword)
        job.cancel()
    }
    
    @Test
    fun `set keyword to search, when duplicated keyword is set then nothing inserted`() = runTest {
        // act
        val duplicatedKeyword = initialSearchHistoryList.first()
        sut.setSearchKeyword(keyword = duplicatedKeyword)

        // assert
        lateinit var collectedSearchHistories: List<SearchHistory>
        val job = launch {
            sut.searchHistories.collect {
                collectedSearchHistories = it
            }
        }
        advanceUntilIdle()

        assertThat(collectedSearchHistories).isEqualTo(initialSearchHistoryList)
        job.cancel()
    }

    @Test
    fun `delete search history, the deleted one must not be in collected histories`() = runTest {
        // act
        val deletedHistory = initialSearchHistoryList.first()
        sut.deleteSearchHistory(searchHistory = deletedHistory)

        // assert
        lateinit var collectedSearchHistories: List<SearchHistory>
        val job = launch {
            sut.searchHistories.collect {
                collectedSearchHistories = it
            }
        }
        advanceUntilIdle()

        assertThat(collectedSearchHistories).doesNotContain(deletedHistory)
        job.cancel()
    }
}