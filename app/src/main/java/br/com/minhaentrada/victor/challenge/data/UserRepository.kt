package br.com.minhaentrada.victor.challenge.data

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun findByEmail(email: String): User? {
        return userDao.findByEmail(email)
    }
}