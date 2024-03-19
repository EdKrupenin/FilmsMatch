package com.example.domain.model

data class SortingOptionDomain(val description: String, val value: String) {
    companion object {
        val DEFAULT = SortingOptionDomain("По рейтингу", "RATING")
    }
}
