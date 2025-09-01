package br.com.minhaentrada.victor.challenge.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.User
import br.com.minhaentrada.victor.challenge.data.UserRepository
import kotlinx.coroutines.launch
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import br.com.minhaentrada.victor.challenge.data.Event
import br.com.minhaentrada.victor.challenge.data.EventRepository

class MainViewModel(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> = _logoutComplete

    private val _deleteComplete = MutableLiveData<Boolean>()
    val deleteComplete: LiveData<Boolean> = _deleteComplete

    fun loadInitialData(sharedPreferences: SharedPreferences) {
        val userId = sharedPreferences.getLong("LOGGED_IN_USER_ID", -1L)
        if (userId != -1L) {
            viewModelScope.launch {
                _user.value = userRepository.findById(userId)
                eventRepository.getAllEventsFromUser(userId).collect { events ->
                    _events.value = events
                }
            }
        } else {
            _user.value = null
        }
    }

    fun deleteUser(user: User, sharedPreferences: SharedPreferences) {
        viewModelScope.launch {
            userRepository.delete(user)
            sharedPreferences.edit{
                clear()
            }
            _deleteComplete.value = true
        }
    }

    fun logout(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit {
            clear()
        }
        _logoutComplete.value = true
    }
}

class MainViewModelFactory(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(userRepository, eventRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}