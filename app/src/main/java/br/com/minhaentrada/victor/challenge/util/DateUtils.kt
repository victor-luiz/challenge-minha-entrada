package br.com.minhaentrada.victor.challenge.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {

    fun calculateAge(birthDateString: String?): Int? {
        if (birthDateString.isNullOrEmpty()) return null

        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        try {
            val birthDate = format.parse(birthDateString) ?: return null

            val today = Calendar.getInstance()
            val birth = Calendar.getInstance()
            birth.time = birthDate

            var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)

            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                age--
            }

            return age
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}