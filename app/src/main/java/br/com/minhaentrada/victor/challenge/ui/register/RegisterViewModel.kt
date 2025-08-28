package br.com.minhaentrada.victor.challenge.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.User
import br.com.minhaentrada.victor.challenge.data.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    sealed class RegistrationState {
        object Success : RegistrationState()
        object EmailAlreadyExists : RegistrationState()
        object Loading : RegistrationState()
    }

    private val _registrationStatus = MutableLiveData<RegistrationState>()
    val registrationStatus: LiveData<RegistrationState> = _registrationStatus

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registrationStatus.value = RegistrationState.Loading
            val existingUser = repository.findByEmail(email)
            if (existingUser != null) {
                _registrationStatus.value = RegistrationState.EmailAlreadyExists
            } else {
                val newUser = User(username = username, email = email, password = password)
                repository.insert(newUser)
                _registrationStatus.value = RegistrationState.Success
            }
        }
    }
}

class RegisterViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}