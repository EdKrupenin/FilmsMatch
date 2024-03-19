package com.example.domain.cache

interface IContentCache {
    val filmsCache: IFilmsCache
    val genreCache: IGenreCache
    val sortingOptionsCache: ISortingOptionsCache
}
