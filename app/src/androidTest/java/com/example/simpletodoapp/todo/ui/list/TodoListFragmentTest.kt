package com.example.simpletodoapp.todo.ui.list

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasFocus
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.simpletodoapp.R
import com.example.simpletodoapp.di.search_history.SearchHistoryDataModule
import com.example.simpletodoapp.di.todo.TodoDataModule
import com.example.simpletodoapp.search_history.data.repository.FakeSearchHistoryRepository
import com.example.simpletodoapp.search_history.domain.SearchHistory
import com.example.simpletodoapp.search_history.domain.SearchHistoryRepository
import com.example.simpletodoapp.todo.data.repository.FakeTodoRepository
import com.example.simpletodoapp.todo.domain.TodoDetail
import com.example.simpletodoapp.todo.domain.TodoRepository
import com.example.simpletodoapp.util.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
@UninstallModules(TodoDataModule::class, SearchHistoryDataModule::class)
class TodoListFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    lateinit var todoRepository: TodoRepository

    @BindValue
    lateinit var searchHistoryRepository: SearchHistoryRepository

    @Test
    fun clickAddButton_thenNavigateToTodoInsertFragment() {
        // arrange
        val navController = mockk<NavController>(relaxed = true)
        injectWithRequiredListAndLaunchFragment {
            Navigation.setViewNavController(requireView(), navController)
        }

        // act
        onView(withId(R.id.navigate_todo_add_button)).perform(click())

        // assert
        verify { navController.navigate(R.id.action_todoListFragment_to_todoInsertFragment) }
    }

    @Test
    fun clickTodoItem_thenNavigateToTodoDetailFragment() {
        // arrange
        val todoList = (1..3).map { TodoDetail(id = it.toLong(), todo = "Todo $it") }

        val navController = mockk<NavController>(relaxed = true)
        injectWithRequiredListAndLaunchFragment(todoList = todoList) {
            Navigation.setViewNavController(requireView(), navController)
        }

        // act
        val clickedTodo = todoList.first()
        onView(withChild(withText(clickedTodo.todo))).perform(click())

        // assert
        verify {
            navController.navigate(
                TodoListFragmentDirections.actionTodoListFragmentToTodoDetailFragment(
                    todoId = clickedTodo.id
                )
            )
        }
    }

    @Test
    fun clickSearchBar_thenSearchHistorySectionAndClearButtonShouldBeVisible() {
        // arrange
        injectWithRequiredListAndLaunchFragment()
        // act
        onView(withId(R.id.search_bar_edit_text)).perform(click())

        // assert
        onView(withId(R.id.search_history_section)).check(matches(isDisplayed()))
        onView(withId(R.id.search_bar_clear_button)).check(matches(isDisplayed()))
    }

    @Test
    fun clickSearchBar_whenNoSearchHistoryExists_thenNoSearchHistoryTextVisible() {
        // arrange
        injectWithRequiredListAndLaunchFragment()
        val noSearchHistoryText =
            getApplicationContext<Context>().getString(R.string.no_search_history)

        // act
        onView(withId(R.id.search_bar_edit_text)).perform(click())

        // assert
        onView(withText(noSearchHistoryText)).check(matches(isDisplayed()))
        onView(withId(R.id.search_history_recycler_view)).check(matches(not(isDisplayed())))
    }

    @Test
    fun clickSearchBar_whenSearchHistoryExists_thenShowHistoryList() {
        // arrange
        // created 5 search histories
        // as the search history recycler view can only display up to 5 items
        val searchHistoryList = (1..5).map { "Search History $it" }
        injectWithRequiredListAndLaunchFragment(searchHistoryList = searchHistoryList)

        // act
        onView(withId(R.id.search_bar_edit_text)).perform(click())

        // assert
        onView(withId(R.id.search_history_recycler_view)).check(matches(isDisplayed()))
        searchHistoryList.forEach { searchHistory ->
            onView(withText(searchHistory)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun clickSearchBarClearButton_thenClearEditText() {
        // arrange
        injectWithRequiredListAndLaunchFragment()

        onView(withId(R.id.search_bar_edit_text))
            .perform(click(), typeText("search query text"))
            .check(matches(withText("search query text"))) // check if text is typed correctly

        // act
        onView(withId(R.id.search_bar_clear_button)).perform(click())

        // assert
        onView(withId(R.id.search_bar_edit_text)).check(matches(withText("")))
    }

    @Test
    fun searchTodosWithKeyword_thenOnlyShowTodosThatContainKeyword() {
        // arrange
        val searchQuery = "Searchable"

        val targetTodos = (1..3).map {
            TodoDetail(id = it.toLong(), todo = "$searchQuery $it")
        }

        val nonTargetTodos = (4..6).map {
            TodoDetail(id = it.toLong(), todo = "Todo $it")
        }

        injectWithRequiredListAndLaunchFragment(todoList = targetTodos + nonTargetTodos)

        // act
        onView(withId(R.id.search_bar_edit_text))
            .perform(click(), typeText(searchQuery))
            .check(matches(withText(searchQuery)))

        onView(withId(R.id.search_bar_search_button)).perform(click())

        // assert
        targetTodos.forEach { onView(withText(it.todo)).check(matches(isDisplayed())) }
        nonTargetTodos.forEach { onView(withText(it.todo)).check(doesNotExist()) }
    }

    @Test
    fun searchTodosUsingIMEActionButton_thenOnlyShowTodosThatContainKeyword() {
        // arrange
        val searchQuery = "Searchable"

        val targetTodos = (1..3).map {
            TodoDetail(id = it.toLong(), todo = "$searchQuery $it")
        }

        val nonTargetTodos = (4..6).map {
            TodoDetail(id = it.toLong(), todo = "Todo $it")
        }

        injectWithRequiredListAndLaunchFragment(todoList = targetTodos + nonTargetTodos)

        // act
        onView(withId(R.id.search_bar_edit_text))
            .perform(click(), typeText(searchQuery), pressImeActionButton())
            .check(matches(withText(searchQuery)))

        // assert
        targetTodos.forEach { onView(withText(it.todo)).check(matches(isDisplayed())) }
        nonTargetTodos.forEach { onView(withText(it.todo)).check(doesNotExist()) }
    }

    @Test
    fun searchTodosWithKeyword_thenSearchHistoryShouldBeAdded() {
        // arrange
        injectWithRequiredListAndLaunchFragment()

        // act
        val searchQuery = "search query text"
        onView(withId(R.id.search_bar_edit_text))
            .perform(click(), typeText(searchQuery))
            .check(matches(withText(searchQuery)))

        onView(withId(R.id.search_bar_search_button)).perform(click())
        onView(withId(R.id.search_bar_edit_text)).perform(click())

        // assert
        onView(allOf(withText(searchQuery), not(withId(R.id.search_bar_edit_text))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickSearchHistoryItem_thenSearchTodoWithHistoryKeyword() {
        // arrange
        val searchHistoryList = (1..5).map { "Search History $it" }
        injectWithRequiredListAndLaunchFragment(searchHistoryList = searchHistoryList)

        // act
        val clickedSearchHistory = searchHistoryList.first()
        onView(withId(R.id.search_bar_edit_text)).perform(click())
        onView(withText(clickedSearchHistory)).perform(click())

        // assert
        onView(withId(R.id.search_bar_edit_text)).check(matches(withText(clickedSearchHistory)))
    }

    @Test
    fun clickSearchHistoryDeleteButton_thenHistoryDeleted() {
        // arrange
        val searchHistoryList = (1..5).map { "Search History $it" }
        injectWithRequiredListAndLaunchFragment(searchHistoryList = searchHistoryList)

        // act
        val deletedSearchHistory = searchHistoryList.first()
        onView(withId(R.id.search_bar_edit_text)).perform(click())
        onView(
            allOf(
                withParent(withChild(withText(deletedSearchHistory))),
                withId(R.id.search_history_delete_button)
            )
        ).perform(click())

        // assert
        onView(withText(deletedSearchHistory)).check(doesNotExist())
    }

    @Test
    fun pressBackButtonOnSearchMode_thenExitSearchMode() {
        // arrange
        injectWithRequiredListAndLaunchFragment()

        // act, assert
        onView(withId(R.id.search_bar_edit_text))
            .perform(click())
            .check(matches(hasFocus()))

        onView(withId(R.id.search_bar_edit_text))
            .perform(pressBack())
            .check(matches(not(hasFocus())))

        onView(withId(R.id.search_history_section)).check(matches(not(isDisplayed())))
    }

    @Test
    fun pressEmptySpaceOnSearchMode_thenExitSearchMode() {
        // arrange
        injectWithRequiredListAndLaunchFragment()

        // act
        onView(withId(R.id.search_bar_edit_text))
            .perform(click(), closeSoftKeyboard())
            .check(matches(hasFocus()))

        // performing single click on R.id.search_history_empty_space throws PerformException
        // so alternatively click R.id.todo_recycler_view
        onView(withId(R.id.todo_recycler_view)).perform(click())

        // assert
        onView(withId(R.id.search_bar_edit_text)).check(matches(not(hasFocus())))
        onView(withId(R.id.search_history_section)).check(matches(not(isDisplayed())))
    }

    @Test
    fun swipeToDeleteTodo_thenTodoShouldBeDeleted() {
        // arrange
        val todoList = (1..3).map { TodoDetail(id = it.toLong(), todo = "Todo $it") }
        injectWithRequiredListAndLaunchFragment(todoList = todoList)

        // act, assert
        val deletedTodo = todoList.first()
        onView(withChild(withText(deletedTodo.todo))).perform(swipeLeft()).check(doesNotExist())
    }

    // region helper function ======================================================================
    private fun injectWithRequiredListAndLaunchFragment(
        todoList: List<TodoDetail> = emptyList(),
        searchHistoryList: List<SearchHistory> = emptyList(),
        fragmentAction: Fragment.() -> Unit = {},
    ) {
        todoRepository = FakeTodoRepository(initialList = todoList)
        searchHistoryRepository = FakeSearchHistoryRepository(initialList = searchHistoryList)

        hiltRule.inject()
        launchFragmentInHiltContainer<TodoListFragment>(action = fragmentAction)
    }
    // endregion helper function ===================================================================
}