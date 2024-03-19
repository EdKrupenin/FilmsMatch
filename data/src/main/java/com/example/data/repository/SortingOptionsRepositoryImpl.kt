package com.example.data.repository

import com.example.domain.cache.ISortingOptionsCache
import com.example.domain.model.SortingOptionDomain
import com.example.domain.repository.SortingOptionsRepository
import javax.inject.Inject

class SortingOptionsRepositoryImpl @Inject constructor(
    private val sortingOptionsCache: ISortingOptionsCache,
) : BaseRepository(), SortingOptionsRepository {

    override val sortingOptionDomains = listOf(
        SortingOptionDomain("По рейтингу", "RATING"),
        SortingOptionDomain("По количеству голосов", "NUM_VOTE"),
        SortingOptionDomain("По году", "YEAR")
    )

    override var selectedSortingOption: SortingOptionDomain
        get() = sortingOptionsCache.selectedSortingOption
        set(value) {
            sortingOptionsCache.updateSelectedSortingOption(value)
        }
}