package br.com.minhaentrada.victor.challenge.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "events",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Event(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "event_date")
    val eventDate: Date,

    @ColumnInfo(name = "category")
    val category: EventCategory,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "user_id", index = true)
    val userId: Long,
    ) {
}