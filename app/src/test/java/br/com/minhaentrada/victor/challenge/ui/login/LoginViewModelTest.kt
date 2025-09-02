package br.com.minhaentrada.victor.challenge.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.minhaentrada.victor.challenge.data.user.User
import br.com.minhaentrada.victor.challenge.data.user.UserRepository
import br.com.minhaentrada.victor.challenge.util.SecurityUtils
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: UserRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        repository = mockk()
        viewModel = LoginViewModel(repository)
        mockkObject(SecurityUtils)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(SecurityUtils)
    }

    @Test
    fun `loginUser with correct credentials should post Success state`() {
        val testIdentifier = "test@email.com"
        val testPassword = "password123"
        val salt = SecurityUtils.generateSalt()
        val hashedPassword = SecurityUtils.hashPassword(testPassword, salt)
        val fakeUser = User(id = 1, username = "Test", email = testIdentifier, hashedPassword = hashedPassword, salt = salt)
        coEvery { repository.findUserByIdentifier(testIdentifier) } returns fakeUser
        every { SecurityUtils.verifyPassword(testPassword, salt, hashedPassword) } returns true
        viewModel.loginUser(testIdentifier, testPassword)
        val state = viewModel.loginStatus.value
        assert(state is LoginViewModel.LoginState.Success)
        assertEquals(fakeUser, (state as LoginViewModel.LoginState.Success).user)
    }

    @Test
    fun `loginUser with non-existent user should post UserNotFound state`() {
        val testIdentifier = "nonexistent@email.com"
        coEvery { repository.findUserByIdentifier(testIdentifier) } returns null
        viewModel.loginUser(testIdentifier, "any_password")
        val state = viewModel.loginStatus.value
        assertEquals(LoginViewModel.LoginState.UserNotFound, state)
    }

    @Test
    fun `loginUser with wrong password should post InvalidPassword state`() {
        val testIdentifier = "test@email.com"
        val correctPassword = "password123"
        val wrongPassword = "wrong_password"
        val salt = SecurityUtils.generateSalt()
        val hashedPassword = SecurityUtils.hashPassword(correctPassword, salt)
        val fakeUser = User(id = 1, username = "Test", email = testIdentifier, hashedPassword = hashedPassword, salt = salt)
        coEvery { repository.findUserByIdentifier(testIdentifier) } returns fakeUser
        every { SecurityUtils.verifyPassword(wrongPassword, salt, hashedPassword) } returns false
        viewModel.loginUser(testIdentifier, wrongPassword)
        val state = viewModel.loginStatus.value
        assertEquals(LoginViewModel.LoginState.InvalidPassword, state)
    }


}