package br.com.minhaentrada.victor.challenge.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.minhaentrada.victor.challenge.data.User
import br.com.minhaentrada.victor.challenge.data.UserRepository
import br.com.minhaentrada.victor.challenge.util.SecurityUtils
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val instatExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: UserRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        repository = mockk()
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loginUser with correct credentials should post Success state`() {
        val username = "test"
        val email = "test@email.com"
        val password = "123456"
        val salt = SecurityUtils.generateSalt()
        val hashedPassword = SecurityUtils.hashPassword(password, salt)
        val user = User(1, username, email, hashedPassword, salt)

        coEvery { repository.findByEmail(email) } returns user

        mockkObject(SecurityUtils)
        every { SecurityUtils.verifyPassword(password, salt, hashedPassword) } returns true

        viewModel.loginUser(email, password)

        val state = viewModel.loginStatus.value

        assert(state is LoginViewModel.LoginState.Success)
        assertEquals(user, (state as LoginViewModel.LoginState.Success).user)
    }

    @Test
    fun `loginUser with wrong password should post InvalidPassword state`() {
        val username = "test"
        val email = "test@email.com"
        val password = "123456"
        val wrongPassword = "password123"
        val salt = SecurityUtils.generateSalt()
        val hashedPassword = SecurityUtils.hashPassword(password, salt)
        val user = User(1, username, email, hashedPassword, salt)

        coEvery { repository.findByEmail(email) } returns user

        mockkObject(SecurityUtils)
        every { SecurityUtils.verifyPassword(wrongPassword, salt, hashedPassword) } returns false

        viewModel.loginUser(email, wrongPassword)

        val state = viewModel.loginStatus.value
        assertEquals(LoginViewModel.LoginState.InvalidPassword, state)
    }

}