package br.com.minhaentrada.victor.challenge.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun toEventCategory(value: String): EventCategory {
        return enumValueOf<EventCategory>(value)
    }

    @TypeConverter
    fun fromEventCategory(value: EventCategory): String {
        return value.name
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}