package com.example.hw3api.data

import com.example.hw3api.data.local.FavouriteDao
import com.example.hw3api.data.local.FavouriteEntity
import com.example.hw3api.data.remote.CharacterApi
import com.example.hw3api.data.remote.CharacterDto
import com.example.hw3api.data.remote.CharacterResponse
import com.example.hw3api.model.Character as ModelCharacter
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterRepositoryTest {

    private lateinit var api: CharacterApi
    private lateinit var favouriteDao: FavouriteDao
    private lateinit var repository: CharacterRepository

    private val testDto = CharacterDto(1, "Rick Sanchez", "Alive", "Human", "url1")

    @Before
    fun setUp() {
        api = mockk()
        favouriteDao = mockk()
        repository = CharacterRepository(api, favouriteDao)
    }

    @Test
    fun `searchCharacters success returns mapped characters`() = runTest {
        val response = CharacterResponse(listOf(testDto))
        coEvery { api.getCharacters("Rick", 1) } returns response
        coEvery { favouriteDao.getFavouritesIds() } returns emptyList()

        val result = repository.searchCharacters("Rick", 1)

        assertEquals(1, result.size)
        assertEquals("Rick Sanchez", result[0].name)
    }

    @Test
    fun `searchCharacters network error throws IOException`() = runTest {
        coEvery { api.getCharacters(any(), any()) } throws IOException("Network error")

        try {
            repository.searchCharacters("Rick", 1)
            fail("Expected IOException")
        } catch (e: IOException) {
            assertEquals("Network error", e.message)
        }
    }

    @Test
    fun `searchCharacters 404 returns empty list`() = runTest {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 404
        coEvery { api.getCharacters(any(), any()) } throws httpException

        val result = repository.searchCharacters("NotFound", 1)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getCharacter returns correct character`() = runTest {
        coEvery { api.getCharacterById(1) } returns testDto
        coEvery { favouriteDao.getFavouritesIds() } returns listOf(1)

        val result = repository.getCharacter(1)

        assertEquals("Rick Sanchez", result.name)
        assertTrue(result.isFavourite)
    }

    @Test
    fun `toggleFavourite adds character to favourites`() = runTest {
        val character = ModelCharacter(1, "Rick Sanchez", "Alive", "Human", "url1", false)
        coEvery { favouriteDao.upsert(any()) } just Runs
        coEvery { favouriteDao.getFavouritesIds() } returns emptyList()

        repository.toggleFavourite(character)

        coVerify(exactly = 1) { favouriteDao.upsert(any<FavouriteEntity>()) }
    }

    @Test
    fun `toggleFavourite twice does not create duplicate`() = runTest {
        coEvery { favouriteDao.upsert(any()) } just Runs
        coEvery { favouriteDao.deleteById(1) } just Runs
        coEvery { favouriteDao.getFavouritesIds() } returns emptyList()

        repository.toggleFavourite(ModelCharacter(1, "Rick Sanchez", "Alive", "Human", "url1", false))
        coVerify(exactly = 1) { favouriteDao.upsert(any<FavouriteEntity>()) }

        coEvery { favouriteDao.getFavouritesIds() } returns listOf(1)
        repository.toggleFavourite(ModelCharacter(1, "Rick Sanchez", "Alive", "Human", "url1", true))
        coVerify(exactly = 1) { favouriteDao.deleteById(1) }
    }
}