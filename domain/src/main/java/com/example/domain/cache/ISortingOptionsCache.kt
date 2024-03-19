package com.example.domain.cache

import com.example.domain.model.SortingOptionDomain

interface ISortingOptionsCache {
    val selectedSortingOption: SortingOptionDomain
    fun updateSelectedSortingOption(option: SortingOptionDomain)
}
