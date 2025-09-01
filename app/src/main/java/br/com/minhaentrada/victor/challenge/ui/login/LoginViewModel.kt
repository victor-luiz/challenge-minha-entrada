package br.com.minhaentrada.victor.challenge.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.User
import br.com.minhaentrada.victor.challenge.data.UserRepository
import br.com.minhaentrada.victor.challenge.util.SecurityUtils
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    sealed class LoginState {
        data class Success(val user: User) : LoginState()
        object UserNotFound : LoginState()
        object InvalidPassword : LoginState()
        object Loading : LoginState()
    }

    private val _loginStatus = MutableLiveData<LoginState>()
    val loginStatus: LiveData<LoginState> = _loginStatus

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            _loginStatus.value = LoginState.Loading
            val user = repository.findByUsername(username) ?: repository.findByEmail(username)
            if (user == null) {
                _loginStatus.value = LoginState.UserNotFound
            } else {
                verifyUserPassword(user, password)
            }
        }
    }

    private fun verifyUserPassword(user: User, password: String) {
        val isPasswordCorrect = SecurityUtils.verifyPassword(
            password = password,
            salt = user.salt,
            hashedPassword = user.hashedPassword
        )

        if (isPasswordCorrect) {
            _loginStatus.value = LoginState.Success(user)
        } else {
            _loginStatus.value = LoginState.InvalidPassword
        }
    }
}

class LoginViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}