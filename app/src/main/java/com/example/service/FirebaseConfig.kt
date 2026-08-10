package com.example.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FirebaseConfig {
    private val _isFirebaseConfigured = MutableStateFlow(false)
    val isFirebaseConfigured: StateFlow<Boolean> = _isFirebaseConfigured.asStateFlow()

    private val _isDemoMode = MutableStateFlow(true)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    fun initialize() {
        // Safe check for Firebase runtime configuration / credentials
        val hasCredentials = try {
            System.getenv("FIREBASE_PROJECT_ID")?.isNotEmpty() == true ||
            System.getenv("GOOGLE_APPLICATION_CREDENTIALS")?.isNotEmpty() == true
        } catch (e: Exception) {
            false
        }

        _isFirebaseConfigured.value = hasCredentials
        _isDemoMode.value = !hasCredentials
    }
}
