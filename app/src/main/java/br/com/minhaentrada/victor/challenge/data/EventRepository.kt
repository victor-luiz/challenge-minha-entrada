package br.com.minhaentrada.victor.challenge.data

import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {

    suspend fun insert(event: Event) {
        eventDao.insert(event)
    }

    suspend fun getEventById(id: Long): Event? {
        return eventDao.getEventById(id)
    }

    suspend fun update(event: Event) {
        eventDao.update(event)
    }

    suspend fun delete(event: Event) {
        eventDao.delete(event)
    }

    fun getAllEventsFromUser(userId: Long): Flow<List<Event>> {
        return eventDao.getAllEventsFromUser(userId)
    }
}