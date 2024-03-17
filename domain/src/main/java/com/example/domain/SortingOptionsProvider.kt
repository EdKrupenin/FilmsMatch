package com.example.domain

import com.example.data.SortingOption
import javax.inject.Inject

/**
 * Provides a list of sorting options.
 * This interface can be implemented by any class that aims to provide different sorting criteria.
 */
interface SortingOptionsProvider {
    val sortingOptions: List<SortingOption>
}