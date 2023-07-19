package com.example.simpletodoapp.di

import android.content.Context
import androidx.room.Room
import com.example.simpletodoapp.search_history.data.local.SearchHistoryDao
import com.example.simpletodoapp.todo.data.local.TodoDao
import com.example.simpletodoapp.todo.data.local.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): TodoDatabase = Room.databaseBuilder(
        context = context,
        klass = TodoDatabase::class.java,
        name = TodoDatabase.DATABASE_NAME
    ).build()

    @Provides
    fun provideTodoDao(database: TodoDatabase): TodoDao = database.todoDao

    @Provides
    fun provideSearchHistoryDao(database: TodoDatabase): SearchHistoryDao =
        database.searchHistoryDao
}