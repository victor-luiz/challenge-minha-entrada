package br.com.minhaentrada.victor.challenge.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.user.User
import br.com.minhaentrada.victor.challenge.data.user.UserRepository
import kotlinx.coroutines.launch
import br.com.minhaentrada.victor.challenge.data.event.Event
import br.com.minhaentrada.victor.challenge.data.event.EventRepository
import br.com.minhaentrada.victor.challenge.data.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> = _logoutComplete

    private val _deleteComplete = MutableLiveData<Boolean>()
    val deleteComplete: LiveData<Boolean> = _deleteComplete

    fun loadInitialData() {
        val userId = sessionManager.getLoggedInUserId()
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

    fun logout() {
        sessionManager.clearSession()
        _logoutComplete.value = true
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.delete(user)
            sessionManager.clearSession()
            _deleteComplete.value = true
        }
    }
}