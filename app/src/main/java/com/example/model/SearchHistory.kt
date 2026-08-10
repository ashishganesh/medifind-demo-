package com.example.model

data class SearchHistory(
    val id: String,
    val query: String,
    val userId: String,
    val location: String,
    val timestamp: String,
    val resultsCount: Int
)
