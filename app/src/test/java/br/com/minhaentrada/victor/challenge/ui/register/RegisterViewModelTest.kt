package br.com.minhaentrada.victor.challenge.ui.register

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
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var repository: UserRepository
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        repository = mockk()
        viewModel = RegisterViewModel(repository)
        mockkObject(SecurityUtils)
        every { SecurityUtils.generateSalt() } returns ByteArray(16)
        every { SecurityUtils.hashPassword(any(), any()) } returns "hashed_password"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(SecurityUtils)
    }

    @Test
    fun `registerUser with new username and email should post Success state`() {
        val newUser = User(username = "newUser", email = "new@email.com", hashedPassword = "", salt = byteArrayOf())
        coEvery { repository.findByUsername(newUser.username) } returns null
        coEvery { repository.findByEmail(newUser.email) } returns null
        coEvery { repository.insert(any()) } returns Unit
        viewModel.registerUser(newUser.username, newUser.email, "password123")
        val state = viewModel.registrationStatus.value
        assertEquals(RegisterViewModel.RegistrationState.Success, state)
        coVerify(exactly = 1) { repository.insert(any()) }
    }

    @Test
    fun `registerUser with existing username should post UsernameAlreadyExists state`() {
        val existingUsername = "existingUser"
        val fakeExistingUser = mockk<User>()
        coEvery { repository.findByUsername(existingUsername) } returns fakeExistingUser
        viewModel.registerUser(existingUsername, "email@test.com", "password")
        val state = viewModel.registrationStatus.value
        assertEquals(RegisterViewModel.RegistrationState.UsernameAlreadyExists, state)
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun `registerUser with existing email should post EmailAlreadyExists state`() {
        val newUsername = "newUser"
        val existingEmail = "existing@email.com"
        val fakeExistingUser = mockk<User>()
        coEvery { repository.findByUsername(newUsername) } returns null
        coEvery { repository.findByEmail(existingEmail) } returns fakeExistingUser
        viewModel.registerUser(newUsername, existingEmail, "password",)
        val state = viewModel.registrationStatus.value
        assertEquals(RegisterViewModel.RegistrationState.EmailAlreadyExists, state)
        coVerify(exactly = 0) { repository.insert(any()) }
    }
}