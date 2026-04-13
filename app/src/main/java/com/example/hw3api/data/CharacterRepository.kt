package com.example.hw3api.data

import com.example.hw3api.data.remote.CharacterApi
import com.example.hw3api.data.remote.toDomain
import com.example.hw3api.model.Character
import com.example.hw3api.NetworkModule
import retrofit2.HttpException

class CharacterRepository(
    private val api: CharacterApi = NetworkModule.api
) {
    suspend fun searchCharacters(
        query: String,
        page: Int
    ): List<Character> {
        return try {
            api.getCharacters(query, page).results.map { it.toDomain() }
        } catch (e: HttpException) {
            if (e.code() == 404) emptyList() else throw e
        }
    }

    suspend fun getCharacter(id: Int): Character {
        return api.getCharacterById(id).toDomain()
    }
}