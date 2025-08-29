package br.com.minhaentrada.victor.challenge.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.minhaentrada.victor.challenge.data.User
import br.com.minhaentrada.victor.challenge.data.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: UserRepository
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        repository = mockk()
        viewModel = RegisterViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `registerUser with new email should post Success state`() {
        val username = "test"
        val email = "test@email.com"
        val password = "123456"

        coEvery { repository.findByEmail(email) } returns null
        coEvery { repository.insert(any()) } returns Unit

        viewModel.registerUser(username, email, password)
        val state = viewModel.registrationStatus.value
        assertEquals(RegisterViewModel.RegistrationState.Success, state)
        coVerify(exactly = 1) { repository.insert(any()) }
    }

    @Test
    fun `registerUser with existing email should post EmailAlreadyExists state`() {
        val username = "test"
        val email = "teste@email.com"
        val password = "123456"
        val existingUser = mockk<User>()

        coEvery { repository.findByEmail(email) } returns existingUser

        viewModel.registerUser(username, email, password)
        val state = viewModel.registrationStatus.value
        assertEquals(RegisterViewModel.RegistrationState.EmailAlreadyExists, state)
        coVerify(exactly = 0) { repository.insert(any()) }
    }
}