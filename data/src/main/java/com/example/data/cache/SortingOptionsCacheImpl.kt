package com.example.data.cache

import com.example.domain.cache.ISortingOptionsCache
import com.example.domain.model.SortingOptionDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SortingOptionsCacheImpl @Inject constructor() : ISortingOptionsCache {
    private var _selectedSortingOption = SortingOptionDomain.DEFAULT

    override val selectedSortingOption: SortingOptionDomain
        get() = _selectedSortingOption

    override fun updateSelectedSortingOption(option: SortingOptionDomain) {
        _selectedSortingOption = option
    }
}