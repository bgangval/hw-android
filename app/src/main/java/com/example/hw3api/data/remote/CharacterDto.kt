package com.example.hw3api.data.remote

import com.example.hw3api.model.Character

data class CharacterResponse(
    val results: List<CharacterDto>
)

data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val image: String,
)

fun CharacterDto.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        status = status,
        species = species,
        image = image
    )
}