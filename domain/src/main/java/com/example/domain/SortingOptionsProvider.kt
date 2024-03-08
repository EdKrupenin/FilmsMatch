package com.example.domain

import com.example.data.SortingOption
import javax.inject.Inject

interface SortingOptionsProvider {
    val sortingOptions: List<SortingOption>
}
class SortingOptionsProviderImpl @Inject constructor() : SortingOptionsProvider {
    override val sortingOptions = listOf(
        SortingOption("По рейтингу", "RATING"),
        SortingOption("По количеству голосов", "NUM_VOTE"),
        SortingOption("По году", "YEAR")
    )
}