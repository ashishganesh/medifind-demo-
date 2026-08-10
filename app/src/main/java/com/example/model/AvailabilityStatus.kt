package com.example.model

enum class AvailabilityStatus(val label: String) {
    AVAILABLE("Available"),
    LOW_STOCK("Low Stock"),
    OUT_OF_STOCK("Out of Stock");

    companion object {
        fun fromStock(stock: Int): AvailabilityStatus {
            return when {
                stock > 10 -> AVAILABLE
                stock in 1..10 -> LOW_STOCK
                else -> OUT_OF_STOCK
            }
        }
    }
}
