package com.example.domain

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // Определяет модуль Dagger
@InstallIn(SingletonComponent::class) // Устанавливает модуль в SingletonComponent Hilt
abstract class DomainModule {

    @Binds // Указывает Hilt связь между интерфейсом и его реализацией
    @Singleton // Опционально, указывает, что зависимость должна быть синглтоном
    abstract fun bindFilmsRepository(
        movieRepositoryImpl: ContentRepository,
    ): FilmsRepository

    @Binds
    @Singleton
    abstract fun bindGenreRepository(
        genreRepositoryImpl: ContentRepository,
    ): GenreRepository

    @Binds
    @Singleton
    abstract fun bindSortingOptions(
        sortingOptionsProviderImpl: ContentRepository,
    ): SortingOptionsProvider
}
