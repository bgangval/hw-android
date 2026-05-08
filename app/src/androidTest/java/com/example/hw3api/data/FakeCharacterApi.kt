package com.example.hw3api.data

import com.example.hw3api.data.remote.CharacterApi
import com.example.hw3api.data.remote.CharacterDto
import com.example.hw3api.data.remote.CharacterResponse

class FakeCharacterApi : CharacterApi {

    var searchResult: List<CharacterDto> = listOf(
        CharacterDto(1, "Rick", "Alive", "Human", "url"),
        CharacterDto(2, "Morty", "Alive", "Human", "url")
    )

    var shouldThrowError = false
    var errorMessage = "Fake error"

    override suspend fun getCharacters(name: String, page: Int): CharacterResponse {
        if (shouldThrowError) throw RuntimeException(errorMessage)
        return CharacterResponse(searchResult)
    }

    override suspend fun getCharacterById(id: Int): CharacterDto {
        if (shouldThrowError) throw RuntimeException(errorMessage)
        return searchResult.firstOrNull { it.id == id }
            ?: CharacterDto(id, "Unknown", "Unknown", "Unknown", "url")
    }
}