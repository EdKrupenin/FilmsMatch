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
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository

    @Binds // Указывает Hilt связь между интерфейсом и его реализацией
    @Singleton // Опционально, указывает, что зависимость должна быть синглтоном
    abstract fun bindGenreRepository(
        genreRepositoryImpl: GenreRepositoryImpl
    ): GenreRepository


}
