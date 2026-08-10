package com.example.service

import com.example.data.MockData
import com.example.model.User
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserService {

    private val _users = MutableStateFlow<List<User>>(MockData.sampleUsers)
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User>(_users.value.first())
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    fun switchRole(role: UserRole) {
        val userForRole = _users.value.find { it.role == role } ?: User(
            id = "usr_${role.name.lowercase()}",
            name = role.title,
            email = "user@medifind.in",
            role = role
        )
        _currentUser.value = userForRole
    }
}
