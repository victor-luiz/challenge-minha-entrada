package br.com.minhaentrada.victor.challenge.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.event.Event
import br.com.minhaentrada.victor.challenge.data.event.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

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