package br.com.minhaentrada.victor.challenge.data

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun findByEmail(email: String): User? {
        return userDao.findByEmail(email)
    }

    suspend fun findById(userId: Long): User? {
        return userDao.findById(userId)
    }

    suspend fun update(user: User) {
        userDao.update(user)
    }

    suspend fun delete(user: User) {
        userDao.delete(user)
    }
}