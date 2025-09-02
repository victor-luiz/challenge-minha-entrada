package br.com.minhaentrada.victor.challenge.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.user.User
import br.com.minhaentrada.victor.challenge.data.user.UserRepository
import br.com.minhaentrada.victor.challenge.util.SecurityUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    sealed class LoginState {
        data class Success(val user: User) : LoginState()
        object UserNotFound : LoginState()
        object InvalidPassword : LoginState()
        object Loading : LoginState()
    }

    private val _loginStatus = MutableLiveData<LoginState>()
    val loginStatus: LiveData<LoginState> = _loginStatus

    fun loginUser(identifier: String, password: String) {
        viewModelScope.launch {
            _loginStatus.value = LoginState.Loading
            val user = repository.findUserByIdentifier(identifier)
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