package com.example.simpletodoapp.di.todo

import com.example.simpletodoapp.todo.data.repository.TodoRepositoryImpl
import com.example.simpletodoapp.todo.domain.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface TodoDataModule {
    @Binds
    fun bindTodoRepository(impl: TodoRepositoryImpl): TodoRepository
}