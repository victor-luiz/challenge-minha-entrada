package br.com.minhaentrada.victor.challenge.util

import java.security.MessageDigest
import java.security.SecureRandom

object SecurityUtils {
    fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    fun hashPassword(password: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val hashedPasswordBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashedPasswordBytes.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun verifyPassword(password: String, salt: ByteArray, hashedPassword: String): Boolean {
        val hash = hashPassword(password, salt)
        return hash == hashedPassword
    }
}