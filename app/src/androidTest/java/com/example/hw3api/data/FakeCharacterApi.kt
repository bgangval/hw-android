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
    var lastQuery: String = ""
    var lastPage: Int = 0

    override suspend fun getCharacters(name: String, page: Int): CharacterResponse {
        lastQuery = name
        lastPage = page
        if (shouldThrowError) throw RuntimeException(errorMessage)
        if (name.isNotBlank()) {
            return CharacterResponse(searchResult.filter { it.name.contains(name, ignoreCase = true) })
        }
        return CharacterResponse(searchResult)
    }

    override suspend fun getCharacterById(id: Int): CharacterDto {
        if (shouldThrowError) throw RuntimeException(errorMessage)
        return searchResult.firstOrNull { it.id == id }
            ?: throw RuntimeException("Character not found: $id")
    }
}