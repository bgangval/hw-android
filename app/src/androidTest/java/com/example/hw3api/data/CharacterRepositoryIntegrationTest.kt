package com.example.hw3api.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.hw3api.data.local.AppDatabase
import com.example.hw3api.model.Character as ModelCharacter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterRepositoryIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: CharacterRepository
    private lateinit var fakeApi: FakeCharacterApi

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        fakeApi = FakeCharacterApi()
        repository = CharacterRepository(fakeApi, database.favouriteDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `write and read favorites from Room`() = runTest {
        val character = ModelCharacter(1, "Rick", "Alive", "Human", "url", false)
        repository.toggleFavourite(character)

        val favourites = repository.getFavourites()
        assertEquals(1, favourites.size)
        assertEquals("Rick", favourites[0].name)
        assertTrue(favourites[0].isFavourite)
    }

    @Test
    fun `remove from favorites works correctly`() = runTest {
        repository.toggleFavourite(ModelCharacter(1, "Rick", "Alive", "Human", "url", false))
        assertEquals(1, repository.getFavourites().size)

        repository.toggleFavourite(ModelCharacter(1, "Rick", "Alive", "Human", "url", true))
        assertEquals(0, repository.getFavourites().size)
    }

    @Test
    fun `search returns characters with favourite status from Room`() = runTest {
        repository.toggleFavourite(ModelCharacter(1, "Rick", "Alive", "Human", "url", false))

        val results = repository.searchCharacters("", 1)
        val rick = results.find { it.id == 1 }
        assertNotNull(rick)
        assertTrue(rick!!.isFavourite)
    }

    @Test
    fun `API error does not affect Room data`() = runTest {
        repository.toggleFavourite(ModelCharacter(1, "Rick", "Alive", "Human", "url", false))
        fakeApi.shouldThrowError = true

        try {
            repository.searchCharacters("", 1)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Fake error", e.message)
        }

        val favourites = repository.getFavourites()
        assertEquals(1, favourites.size)
    }

    @Test
    fun `getCharacter by id returns correct character`() = runTest {
        val result = repository.getCharacter(1)
        assertEquals("Rick", result.name)
    }
}