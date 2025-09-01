package br.com.minhaentrada.victor.challenge.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.Event
import br.com.minhaentrada.victor.challenge.data.EventRepository
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _deleteComplete = MutableLiveData<Boolean>()
    val deleteComplete: LiveData<Boolean> = _deleteComplete

    fun loadEvent(eventId: Long) {
        if (eventId == -1L) {
            _event.value = null
            return
        }
        viewModelScope.launch {
            _event.value = repository.getEventById(eventId)
        }
    }

    fun deleteEvent() {
        _event.value?.let { eventToDelete ->
            viewModelScope.launch {
                repository.delete(eventToDelete)
                _deleteComplete.value = true
            }
        }
    }
}


class EventViewModelFactory(private val repository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}