package br.com.minhaentrada.victor.challenge.ui.main

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.minhaentrada.victor.challenge.data.User
import br.com.minhaentrada.victor.challenge.data.UserRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: UserRepository
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        repository = mockk()
        sharedPreferences = mockk()
        sharedPreferencesEditor = mockk()
        every { sharedPreferences.edit() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.clear() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.apply() } returns Unit
        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserData with valid userId should fetch user and post to LiveData`() {
        val userId = 1L
        val user = User(userId, "Test", "test@email.com", "", byteArrayOf())

        every { sharedPreferences.getLong("LOGGED_IN_USER_ID", -1L) } returns userId
        coEvery { repository.findById(userId) } returns user

        viewModel.loadInitialData(sharedPreferences)
        val userFromLiveData = viewModel.user.value
        assertEquals(user, userFromLiveData)
    }

    @Test
    fun `loadUserData with invalid userId should post null to LiveData`() {
        every { sharedPreferences.getLong("LOGGED_IN_USER_ID", -1L) } returns -1L

        viewModel.loadInitialData(sharedPreferences)
        val userFromLiveData = viewModel.user.value
        assertEquals(null, userFromLiveData)
    }

    @Test
    fun `logout should clear SharedPreferences and post to logoutComplete LiveData`() {
        viewModel.logout(sharedPreferences)

        val isLogoutComplete = viewModel.logoutComplete.value
        verify(exactly = 1) { sharedPreferencesEditor.clear() }
        verify(exactly = 1) { sharedPreferencesEditor.apply() }
        assertTrue(isLogoutComplete ?: false)
    }
}