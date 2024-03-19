package com.example.data

import com.example.data.cache.ContentCacheImpl
import com.example.data.cache.FilmsCacheImpl
import com.example.data.cache.GenreCacheImpl
import com.example.data.cache.SortingOptionsCacheImpl
import com.example.data.repository.FilmsRepositoryImpl
import com.example.data.repository.GenreRepositoryImpl
import com.example.data.repository.SortingOptionsRepositoryImpl
import com.example.domain.cache.IContentCache
import com.example.domain.cache.IFilmsCache
import com.example.domain.cache.IGenreCache
import com.example.domain.cache.ISortingOptionsCache
import com.example.domain.repository.FilmsRepository
import com.example.domain.repository.GenreRepository
import com.example.domain.repository.SortingOptionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindFilmsRepository(
        filmRepositoryImpl: FilmsRepositoryImpl,
    ): FilmsRepository

    @Binds

    @Singleton
    abstract fun bindGenreRepository(
        genreRepositoryImpl: GenreRepositoryImpl,
    ): GenreRepository

    @Binds
    @Singleton
    abstract fun bindSortingOptions(
        sortingOptionsProviderImpl: SortingOptionsRepositoryImpl,
    ): SortingOptionsRepository

    @Binds
    @Singleton
    internal abstract fun bindFilmsCache(impl: FilmsCacheImpl): IFilmsCache

    @Binds
    @Singleton
    internal abstract fun bindGenreCache(impl: GenreCacheImpl): IGenreCache

    @Binds
    @Singleton
    internal abstract fun bindSortingOptionsCache(impl: SortingOptionsCacheImpl): ISortingOptionsCache

    @Binds
    internal abstract fun bindContentCache(contentCacheImpl: ContentCacheImpl): IContentCache
}
