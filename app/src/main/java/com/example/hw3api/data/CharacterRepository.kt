package com.example.hw3api.data

import com.example.hw3api.data.local.FavouriteDao
import com.example.hw3api.data.local.toDomain as favouriteToDomain
import com.example.hw3api.data.local.toFavouriteEntity
import com.example.hw3api.data.remote.CharacterApi
import com.example.hw3api.data.remote.toDomain as dtoToDomain
import com.example.hw3api.model.Character
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository @Inject constructor(
    private val api: CharacterApi,
    private val favouriteDao: FavouriteDao
) {
    suspend fun searchCharacters(
        query: String,
        page: Int
    ): List<Character> {
        return try {
            val response = api.getCharacters(query, page)

            val favouriteIds = try {
                favouriteDao.getFavouritesIds().toSet()
            } catch (e: Exception) {
                emptySet()
            }

            response.results.map { dto ->
                val character = dto.dtoToDomain()
                character.copy(isFavourite = character.id in favouriteIds)
            }
        } catch (e: HttpException) {
            if (e.code() == 404) {
                emptyList()
            } else {
                throw e
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun getCharacter(id: Int): Character {
        val dto = api.getCharacterById(id)
        val character = dto.dtoToDomain()
        val favouriteIds = favouriteDao.getFavouritesIds().toSet()
        return character.copy(isFavourite = character.id in favouriteIds)
    }

    suspend fun getFavourites(): List<Character> {
        return try {
            favouriteDao.getFavourites().map { it.favouriteToDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getFavouritesIds(): Set<Int> {
        return try {
            favouriteDao.getFavouritesIds().toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    suspend fun toggleFavourite(character: Character) {
        if (character.isFavourite) {
            favouriteDao.deleteById(character.id)
        } else {
            favouriteDao.upsert(character.toFavouriteEntity())
        }
    }
}