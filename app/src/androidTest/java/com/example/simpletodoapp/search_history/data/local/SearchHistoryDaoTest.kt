package com.example.simpletodoapp.search_history.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.simpletodoapp.todo.data.local.TodoDatabase
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
class SearchHistoryDaoTest {
    private lateinit var sut: SearchHistoryDao
    private lateinit var database: TodoDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = TodoDatabase::class.java
        ).build()

        sut = database.searchHistoryDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getSearchHistory_thenSuccessfullyGetHistoriesFromDatabase() = runTest {
        // arrange
        val searchHistories = (1..10).createSearchHistoryEntityFromRange().onEach {
            sut.insertSearchHistory(searchHistory = it)
        }

        // act
        lateinit var collectedHistories: List<SearchHistoryEntity>
        val job = launch {
            sut.getSearchHistory().collect {
                collectedHistories = it
            }
        }
        advanceUntilIdle()

        // assert
        assertThat(collectedHistories).isEqualTo(searchHistories)
        job.cancel()
    }

    @Test
    fun insertNewSearchHistory_thenSuccessfullyInsertedIntoDatabase() = runTest {
        // arrange
        (1..10).createSearchHistoryEntityFromRange().forEach {
            sut.insertSearchHistory(searchHistory = it)
        }

        // act
        val newSearchHistory = SearchHistoryEntity(query = "New search history")
        sut.insertSearchHistory(searchHistory = newSearchHistory)

        // assert
        lateinit var collectedHistories: List<SearchHistoryEntity>
        val job = launch {
            sut.getSearchHistory().collect {
                collectedHistories = it
            }
        }
        advanceUntilIdle()

        assertThat(collectedHistories).contains(newSearchHistory)
        job.cancel()
    }

    @Test
    fun insertSearchHistory_whenKeywordAlreadyExist_thenNothingChange() = runTest {
        // arrange
        val searchHistories = (1..10).createSearchHistoryEntityFromRange().onEach {
            sut.insertSearchHistory(searchHistory = it)
        }

        // act
        val duplicatedKeyword = searchHistories.first()
        sut.insertSearchHistory(searchHistory = duplicatedKeyword)

        // assert
        lateinit var collectedHistories: List<SearchHistoryEntity>
        val job = launch {
            sut.getSearchHistory().collect {
                collectedHistories = it
            }
        }
        advanceUntilIdle()

        assertThat(collectedHistories).isEqualTo(searchHistories)
        job.cancel()
    }

    @Test
    fun deleteSearchHistory_thenNotIncludedInCollectedListFromDatabase() = runTest {
        // arrange
        val searchHistories = (1..10).createSearchHistoryEntityFromRange().onEach {
            sut.deleteSearchHistory(searchHistory = it)
        }

        // act
        val deletedSearchHistory = searchHistories.first()
        sut.deleteSearchHistory(searchHistory = deletedSearchHistory)

        // assert
        lateinit var collectedHistories: List<SearchHistoryEntity>
        val job = launch {
            sut.getSearchHistory().collect {
                collectedHistories = it
            }
        }
        advanceUntilIdle()

        assertThat(collectedHistories).doesNotContain(deletedSearchHistory)
        job.cancel()
    }

    // region helper function ======================================================================
    private fun IntRange.createSearchHistoryEntityFromRange(): List<SearchHistoryEntity> = map {
        SearchHistoryEntity(query = "Search history $it")
    }
    // endregion helper function ===================================================================
}