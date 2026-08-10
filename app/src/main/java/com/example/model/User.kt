package com.example.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val pharmacyId: String? = null,
    val isVerified: Boolean = true,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
