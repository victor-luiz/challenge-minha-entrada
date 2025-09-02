package br.com.minhaentrada.victor.challenge.data

import androidx.room.TypeConverter
import br.com.minhaentrada.victor.challenge.data.event.EnumEventCategory
import java.util.Date

class Converters {

    @TypeConverter
    fun toEventCategory(value: String): EnumEventCategory {
        return enumValueOf<EnumEventCategory>(value)
    }

    @TypeConverter
    fun fromEventCategory(value: EnumEventCategory): String {
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