package br.com.minhaentrada.victor.challenge.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.User
import br.com.minhaentrada.victor.challenge.data.UserRepository
import kotlinx.coroutines.launch

class EditProfileViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User?> = _user

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

     fun loadUser(userId: Long) {
         viewModelScope.launch {
             _user.value = repository.findById(userId)
         }
     }

    fun updateUser(
        userId: Long,
        newUsername: String,
        newBirthDate: String,
        newState: String,
        newCity: String
    ) {
        viewModelScope.launch {
            val user = repository.findById(userId)
            if (user != null) {
                val updateUser = user.copy(
                    username = newUsername,
                    birthDate = newBirthDate,
                    city = newCity,
                    state = newState
                )
                repository.update(updateUser)
                _updateStatus.value = true
            }
        }
    }
}

class EditProfileViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}