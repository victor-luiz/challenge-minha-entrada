package br.com.minhaentrada.victor.challenge.ui.event

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.minhaentrada.victor.challenge.data.event.Event
import br.com.minhaentrada.victor.challenge.data.event.EventRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EventViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: EventRepository
    private lateinit var viewModel: EventViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        repository = mockk(relaxed = true)
        viewModel = EventViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadEvent with valid id should fetch event and update LiveData`() {
        val testEventId = 1L
        val fakeEvent = mockk<Event>()
        coEvery { repository.getEventById(testEventId) } returns fakeEvent
        viewModel.loadEvent(testEventId)
        assertEquals(fakeEvent, viewModel.event.value)
    }

    @Test
    fun `loadEvent with invalid id should result in null LiveData`() {
        val invalidEventId = -1L
        viewModel.loadEvent(invalidEventId)
        assertEquals(null, viewModel.event.value)
        coVerify(exactly = 0) { repository.getEventById(any()) }
    }

    @Test
    fun `deleteEvent should call repository delete and post completion`() {
        val fakeEvent = mockk<Event>()
        (viewModel.event as androidx.lifecycle.MutableLiveData).value = fakeEvent
        coEvery { repository.delete(fakeEvent) } returns Unit
        viewModel.deleteEvent()
        coVerify(exactly = 1) { repository.delete(fakeEvent) }
        assertTrue(viewModel.deleteComplete.value ?: false)
    }
}