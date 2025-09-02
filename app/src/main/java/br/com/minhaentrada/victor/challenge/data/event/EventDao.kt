package br.com.minhaentrada.victor.challenge.data.event

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: Event)

    @Update
    suspend fun update(user: Event)

    @Delete
    suspend fun delete(user: Event)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event?

    @Query("SELECT * FROM events WHERE user_id = :userId ORDER BY event_date DESC")
    fun getAllEventsFromUser(userId: Long): Flow<List<Event>>
}