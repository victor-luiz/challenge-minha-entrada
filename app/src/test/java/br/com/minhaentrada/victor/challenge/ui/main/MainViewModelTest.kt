package br.com.minhaentrada.victor.challenge.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.minhaentrada.victor.challenge.data.event.Event
import br.com.minhaentrada.victor.challenge.data.event.EventRepository
import br.com.minhaentrada.victor.challenge.data.SessionManager
import br.com.minhaentrada.victor.challenge.data.user.User
import br.com.minhaentrada.victor.challenge.data.user.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Agora precisamos de mocks para TODAS as dependências da ViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var eventRepository: EventRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        // Criando os mocks
        userRepository = mockk(relaxed = true)
        eventRepository = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)

        // Instanciando a ViewModel com os mocks
        viewModel = MainViewModel(userRepository, eventRepository, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadInitialData with valid user should load user and events`() {
        val testUserId = 1L
        val fakeUser = mockk<User>()
        val fakeEventList = listOf(mockk<Event>(), mockk<Event>())
        val fakeEventFlow = flowOf(fakeEventList)
        every { sessionManager.getLoggedInUserId() } returns testUserId
        coEvery { userRepository.findById(testUserId) } returns fakeUser
        every { eventRepository.getAllEventsFromUser(testUserId) } returns fakeEventFlow
        viewModel.loadInitialData()
        assertEquals(fakeUser, viewModel.user.value)
        assertEquals(fakeEventList, viewModel.events.value)
    }

    @Test
    fun `loadInitialData with no logged in user should result in null user`() {
        every { sessionManager.getLoggedInUserId() } returns -1L
        viewModel.loadInitialData()
        assertEquals(null, viewModel.user.value)
        coVerify(exactly = 0) { eventRepository.getAllEventsFromUser(any()) }
    }

    @Test
    fun `logout should clear session and post completion`() {
        viewModel.logout()
        verify(exactly = 1) { sessionManager.clearSession() }
        assertTrue(viewModel.logoutComplete.value ?: false)
    }
    @Test
    fun `deleteUser should call repository delete and clear session`() {
        val fakeUser = mockk<User>()
        coEvery { userRepository.delete(fakeUser) } returns Unit
        every { sessionManager.clearSession() } returns Unit
        viewModel.deleteUser(fakeUser)
        coVerify(exactly = 1) { userRepository.delete(fakeUser) }
        verify(exactly = 1) { sessionManager.clearSession() }
        assertTrue(viewModel.deleteComplete.value ?: false)
    }
}