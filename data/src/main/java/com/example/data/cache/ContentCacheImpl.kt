package com.example.data.cache

import com.example.domain.cache.IContentCache
import com.example.domain.cache.IFilmsCache
import com.example.domain.cache.IGenreCache
import com.example.domain.cache.ISortingOptionsCache
import javax.inject.Inject

internal class ContentCacheImpl @Inject constructor(
    override val filmsCache: IFilmsCache,
    override val genreCache: IGenreCache,
    override val sortingOptionsCache: ISortingOptionsCache
) : IContentCache {
}