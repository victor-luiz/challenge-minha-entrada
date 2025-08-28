package br.com.minhaentrada.victor.challenge.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.User
import br.com.minhaentrada.victor.challenge.data.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    sealed class LoginState {
        data class Success(val user: User) : LoginState()
        object UserNotFound : LoginState()
        object InvalidPassword : LoginState()
        object Loading : LoginState()
    }

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val user = repository.findByEmail(username)
            if (user == null) {
                _loginState.value = LoginState.UserNotFound
            } else {
                if (user.password == password) {
                    _loginState.value = LoginState.Success(user)
                } else {
                    _loginState.value = LoginState.InvalidPassword
                }
            }
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