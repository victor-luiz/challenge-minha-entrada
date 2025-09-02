package br.com.minhaentrada.victor.challenge.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.user.User
import br.com.minhaentrada.victor.challenge.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    sealed class UpdateState {
        object Success : UpdateState()
        object UsernameAlreadyExists : UpdateState()
        object Idle : UpdateState()
    }
    private val _updateStatus = MutableLiveData<UpdateState>(UpdateState.Idle)
    val updateStatus: LiveData<UpdateState> = _updateStatus

    fun loadUser(userId: Long) {
        viewModelScope.launch {
            _user.value = repository.findById(userId)
        }
    }

    fun updateUser(
        newUsername: String
    ) {
        _user.value?.let { currentUser ->
            viewModelScope.launch {
                val userWithSameName = repository.findByUsername(newUsername)
                if (userWithSameName != null && userWithSameName.id != currentUser.id) {
                    _updateStatus.value = UpdateState.UsernameAlreadyExists
                } else {
                    val updatedUser = currentUser.copy(
                        username = newUsername
                    )
                    repository.update(updatedUser)
                    _updateStatus.value = UpdateState.Success
                }

            }
        }
    }
}