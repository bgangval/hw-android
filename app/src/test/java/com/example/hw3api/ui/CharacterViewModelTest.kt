package com.example.hw3api.ui

import com.example.hw3api.data.CharacterRepository
import com.example.hw3api.model.Character
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import kotlinx.coroutines.delay

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterViewModelTest {

    @get:Rule
    val testDispatcher = TestDispatcherRule()

    private lateinit var repository: CharacterRepository
    private lateinit var viewModel: CharacterViewModel

    private val testCharacters = listOf(
        Character(1, "Rick Sanchez", "Alive", "Human", "url1", false),
        Character(2, "Morty Smith", "Alive", "Human", "url2", true)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher.testDispatcher)
        repository = mockk()
        coEvery { repository.getFavourites() } returns emptyList()
        coEvery { repository.getFavouritesIds() } returns emptySet()
        coEvery { repository.searchCharacters("", any()) } returns testCharacters
    }

    @Test
    fun `initial state is Loading`() = runTest {
        coEvery { repository.searchCharacters(any(), any()) } coAnswers {
            delay(1000)
            testCharacters
        }
        viewModel = CharacterViewModel(repository)

        assertTrue(viewModel.listState.isLoading)
        assertNull(viewModel.listState.errorMessage)
        assertTrue(viewModel.listState.characters.isEmpty())

        advanceUntilIdle()
        assertEquals(2, viewModel.listState.characters.size)
    }

    @Test
    fun `successful search updates state`() = runTest {
        coEvery { repository.searchCharacters("Rick", 1) } returns testCharacters
        viewModel = CharacterViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchChange("Rick")
        advanceUntilIdle()

        assertFalse(viewModel.listState.isLoading)
        assertEquals(2, viewModel.listState.characters.size)
        assertNull(viewModel.listState.errorMessage)
    }

    @Test
    fun `error during search updates state to Error`() = runTest {
        coEvery { repository.searchCharacters("Rick", 1) } throws IOException("Network error")
        viewModel = CharacterViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchChange("Rick")
        advanceUntilIdle()

        assertNotNull(viewModel.listState.errorMessage)
        assertTrue(viewModel.listState.errorMessage!!.contains("Network error"))
    }

    @Test
    fun `retry after error re-executes search exactly once`() = runTest {
        coEvery { repository.searchCharacters("Rick", 1) } throws IOException("Error")
        viewModel = CharacterViewModel(repository)
        advanceUntilIdle()
        viewModel.onSearchChange("Rick")
        advanceUntilIdle()
        assertNotNull(viewModel.listState.errorMessage)

        coEvery { repository.searchCharacters("Rick", 1) } returns testCharacters
        viewModel.retry()
        advanceUntilIdle()

        assertNull(viewModel.listState.errorMessage)
        assertEquals(2, viewModel.listState.characters.size)
        coVerify(exactly = 2) { repository.searchCharacters("Rick", 1) }
    }

    @Test
    fun `loadCharacter updates detailState`() = runTest {
        val character = testCharacters[0]
        coEvery { repository.getCharacter(1) } returns character
        viewModel = CharacterViewModel(repository)
        advanceUntilIdle()

        viewModel.loadCharacter(1)
        advanceUntilIdle()

        assertTrue(viewModel.detailState is DetailUiState.Success)
        val state = viewModel.detailState as DetailUiState.Success
        assertEquals("Rick Sanchez", state.character.name)
    }

    @Test
    fun `empty search result clears characters`() = runTest {
        coEvery { repository.searchCharacters("Unknown", 1) } returns emptyList()
        viewModel = CharacterViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchChange("Unknown")
        advanceUntilIdle()

        assertFalse(viewModel.listState.isLoading)
        assertTrue(viewModel.listState.characters.isEmpty())
    }

    @Test
    fun `rapid search ignores stale response`() = runTest {
        coEvery { repository.searchCharacters("Rick", 1) } coAnswers {
            delay(1000)
            testCharacters
        }
        coEvery { repository.searchCharacters("Morty", 1) } returns listOf(testCharacters[1])
        viewModel = CharacterViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchChange("Rick")
        advanceTimeBy(100)
        viewModel.onSearchChange("Morty")
        advanceUntilIdle()

        assertEquals("Morty", viewModel.listState.searchQuery)
        assertEquals(1, viewModel.listState.characters.size)
        assertEquals("Morty Smith", viewModel.listState.characters[0].name)
        assertNull(viewModel.listState.errorMessage)
    }

    @Test
    fun `search produces Loading then Success`() = runTest {
        coEvery { repository.searchCharacters("Rick", 1) } returns testCharacters
        viewModel = CharacterViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.listState.isLoading)

        viewModel.onSearchChange("Rick")
        advanceUntilIdle()

        assertEquals("Rick", viewModel.listState.searchQuery)
        assertFalse(viewModel.listState.isLoading)
        assertEquals(2, viewModel.listState.characters.size)
    }
}