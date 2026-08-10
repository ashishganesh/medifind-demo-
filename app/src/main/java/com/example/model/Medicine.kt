package com.example.model

data class Medicine(
    val id: String,
    val name: String,
    val genericName: String,
    val category: String,
    val form: String, // e.g. "Tablet", "Syrup", "Injection"
    val strength: String, // e.g. "500mg", "10ml"
    val description: String,
    val dosageInfo: String,
    val requiresPrescription: Boolean = false,
    val genericAlternatives: List<String> = emptyList()
)
