package br.com.minhaentrada.victor.challenge.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "hashedPassword")
    val hashedPassword: String,

    @ColumnInfo(name = "salt")
    val salt: ByteArray,

    @ColumnInfo(name = "birth_date")
    val birthDate: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (hashedPassword != other.hashedPassword) return false
        if (!salt.contentEquals(other.salt)) return false
        if (birthDate != other.birthDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + hashedPassword.hashCode()
        result = 31 * result + salt.contentHashCode()
        result = 31 * result + (birthDate?.hashCode() ?: 0)
        return result
    }

}
