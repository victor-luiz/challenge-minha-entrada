package br.com.minhaentrada.victor.challenge.ui.register

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
class RegisterViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    sealed class RegistrationState {
        object Success : RegistrationState()
        object EmailAlreadyExists : RegistrationState()
        object UsernameAlreadyExists : RegistrationState()
        object Loading : RegistrationState()
    }

    private val _registrationStatus = MutableLiveData<RegistrationState>()
    val registrationStatus: LiveData<RegistrationState> = _registrationStatus

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registrationStatus.value = RegistrationState.Loading

            if (repository.findByUsername(username) != null) {
                _registrationStatus.value = RegistrationState.UsernameAlreadyExists
                return@launch
            }

            if (repository.findByEmail(email) != null) {
                _registrationStatus.value = RegistrationState.EmailAlreadyExists
                return@launch
            }

            val salt = SecurityUtils.generateSalt()
            val hashedPassword = SecurityUtils.hashPassword(password, salt)
            val user = User(
                username = username,
                email = email,
                hashedPassword = hashedPassword,
                salt = salt
            )
            repository.insert(user)
            _registrationStatus.value = RegistrationState.Success
        }
    }
}