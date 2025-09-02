package br.com.minhaentrada.victor.challenge.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.minhaentrada.victor.challenge.data.user.User
import br.com.minhaentrada.victor.challenge.data.user.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
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
class EditProfileViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: UserRepository
    private lateinit var viewModel: EditProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        repository = mockk(relaxed = true)
        viewModel = EditProfileViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser should fetch user from repository and update LiveData`() {
        val testUserId = 1L
        val fakeUser = mockk<User>()
        coEvery { repository.findById(testUserId) } returns fakeUser
        viewModel.loadUser(testUserId)
        assertEquals(fakeUser, viewModel.user.value)
    }

    @Test
    fun `updateUser should call repository update with correctly modified user`() {
        val testUserId = 1L
        val originalUsername = "Victor"
        val newUsername = "Victor Luiz"
        val originalUser = User(
            id = testUserId,
            username = originalUsername,
            email = "test@email.com",
            hashedPassword = "hash",
            salt = byteArrayOf(),
        )
        (viewModel.user as androidx.lifecycle.MutableLiveData).value = originalUser
        val userSlot = slot<User>()
        coEvery { repository.update(capture(userSlot)) } returns Unit
        coEvery { repository.findByUsername(newUsername) } returns null
        viewModel.updateUser(
            newUsername = newUsername
        )
        coVerify(exactly = 1) { repository.update(any()) }
        assertEquals(newUsername, userSlot.captured.username)
        assert(viewModel.updateStatus.value is EditProfileViewModel.UpdateState.Success)
    }

    @Test
    fun `updateUser with already existing username should post UsernameAlreadyExists state`() {
        val currentUserId = 1L
        val anotherUserId = 2L
        val newUsername = "ana"
        val currentUser = User(
            id = currentUserId,
            username = "victor",
            email = "victor@email.com",
            hashedPassword = "hash_victor",
            salt = byteArrayOf(1, 2, 3)
        )
        val existingUserWithSameName = User(
            id = anotherUserId,
            username = newUsername,
            email = "ana@email.com",
            hashedPassword = "hash_ana",
            salt = byteArrayOf(4, 5, 6)
        )
        (viewModel.user as androidx.lifecycle.MutableLiveData).value = currentUser
        coEvery { repository.findByUsername(newUsername) } returns existingUserWithSameName
        viewModel.updateUser(
            newUsername = newUsername,
        )
        assertEquals(EditProfileViewModel.UpdateState.UsernameAlreadyExists, viewModel.updateStatus.value)
        coVerify(exactly = 0) { repository.update(any()) }
    }
}