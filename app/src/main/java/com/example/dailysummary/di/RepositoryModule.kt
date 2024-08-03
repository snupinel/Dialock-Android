package com.example.dailysummary.di

import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.data.PrefRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindPrefRepository(impl: PrefRepositoryImpl): PrefRepository
}