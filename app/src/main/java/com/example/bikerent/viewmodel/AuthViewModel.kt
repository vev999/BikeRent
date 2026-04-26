package com.example.bikerent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bikerent.data.DataSource
import com.example.bikerent.data.repository.UserRepository
import com.example.bikerent.data.util.HashUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: Long, val name: String, val email: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Holds the current session after login/register
    var currentUserId: Long = -1L
        private set
    var currentUserName: String = ""
        private set
    var currentUserEmail: String = ""
        private set

    val isAdmin: Boolean
        get() = DataSource.seededAdminUsers.any { it.email == currentUserEmail }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Wypełnij wszystkie pola")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val hash = HashUtils.sha256(password)
            val user = userRepository.login(email.trim(), hash)
            if (user != null) {
                currentUserId = user.id
                currentUserName = user.name
                currentUserEmail = user.email
                _authState.value = AuthState.Success(user.id, user.name, user.email)
            } else {
                _authState.value = AuthState.Error("Nieprawidłowy e-mail lub hasło")
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Wypełnij wszystkie pola")
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Hasła nie są zgodne")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Hasło musi mieć co najmniej 6 znaków")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val hash = HashUtils.sha256(password)
            val result = userRepository.register(email.trim(), name.trim(), hash)
            result.fold(
                onSuccess = { user ->
                    currentUserId = user.id
                    currentUserName = user.name
                    currentUserEmail = user.email
                    _authState.value = AuthState.Success(user.id, user.name, user.email)
                },
                onFailure = { e ->
                    _authState.value = AuthState.Error(e.message ?: "Błąd rejestracji")
                }
            )
        }
    }

    fun updateUserData(name: String, email: String) {
        viewModelScope.launch {
            userRepository.updateUser(currentUserId, name, email)
            currentUserName = name
            currentUserEmail = email
        }
    }

    fun logout() {
        currentUserId = -1L
        currentUserName = ""
        currentUserEmail = ""
        _authState.value = AuthState.Idle
    }

    fun resetError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}

class AuthViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AuthViewModel(userRepository) as T
}
