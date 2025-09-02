package br.com.minhaentrada.victor.challenge.ui.event

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.minhaentrada.victor.challenge.data.event.Event
import br.com.minhaentrada.victor.challenge.data.event.EnumEventCategory
import br.com.minhaentrada.victor.challenge.data.event.EventRepository
import io.mockk.*
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
import java.util.Date

@ExperimentalCoroutinesApi
class AddEditEventViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: EventRepository
    private lateinit var viewModel: AddEditEventViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        repository = mockk(relaxed = true)
        viewModel = AddEditEventViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveEvent when event is new should call repository insert`() {
        val newEventData = mockk<Event>()
        coEvery { repository.insert(any()) } returns Unit
        viewModel.saveEvent(
            userId = 1L,
            title = "New Event",
            description = "Desc",
            eventDate = Date(),
            category = EnumEventCategory.PARTY,
            city = "City",
            state = "State"
        )
        coVerify(exactly = 1) { repository.insert(any()) }
        coVerify(exactly = 0) { repository.update(any()) }
        assertTrue(viewModel.saveComplete.value ?: false)
    }

    @Test
    fun `saveEvent when event exists should call repository update`() {
        val existingEventId = 1L
        val existingEvent = Event(id = existingEventId, userId = 1L, title = "Old Title", description = "", eventDate = Date(), category = EnumEventCategory.SHOW, city = "", state = "")
        viewModel.loadEvent(existingEventId)
        (viewModel.event as androidx.lifecycle.MutableLiveData).value = existingEvent
        val eventSlot = slot<Event>()
        coEvery { repository.update(capture(eventSlot)) } returns Unit
        val newTitle = "Updated Title"
        viewModel.saveEvent(
            userId = 1L,
            title = newTitle,
            description = "New Desc",
            eventDate = Date(),
            category = EnumEventCategory.SPORTS,
            city = "New City",
            state = "New State"
        )
        coVerify(exactly = 0) { repository.insert(any()) }
        coVerify(exactly = 1) { repository.update(any()) }
        assertEquals(newTitle, eventSlot.captured.title)
        assertEquals(existingEventId, eventSlot.captured.id)

        assertTrue(viewModel.saveComplete.value ?: false)
    }

    @Test
    fun `loadEvent should fetch event and update LiveData`() {
        val testEventId = 1L
        val fakeEvent = mockk<Event>()
        coEvery { repository.getEventById(testEventId) } returns fakeEvent
        viewModel.loadEvent(testEventId)
        assertEquals(fakeEvent, viewModel.event.value)
    }
}