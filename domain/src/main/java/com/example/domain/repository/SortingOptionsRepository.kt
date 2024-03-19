package com.example.domain.repository

import com.example.domain.model.SortingOptionDomain

/**
 * Provides a list of sorting options.
 * This interface can be implemented by any class that aims to provide different sorting criteria.
 */
interface SortingOptionsRepository {
    val sortingOptionDomains: List<SortingOptionDomain>

    var selectedSortingOption : SortingOptionDomain
}