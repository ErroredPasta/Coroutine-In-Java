package com.example.simpletodoapp.di.search_history

import com.example.simpletodoapp.search_history.data.repository.SearchHistoryRepositoryImpl
import com.example.simpletodoapp.search_history.domain.SearchHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
interface SearchHistoryDataModule {
    @Binds
    fun bindSearchHistoryRepository(impl: SearchHistoryRepositoryImpl): SearchHistoryRepository
}