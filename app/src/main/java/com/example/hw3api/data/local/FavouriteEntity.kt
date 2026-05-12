package com.example.hw3api.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hw3api.model.Character

@Entity(tableName = "favourite_character")
data class FavouriteEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val image: String
)

fun FavouriteEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    image = image,
    isFavourite = true
)

fun Character.toFavouriteEntity(): FavouriteEntity = FavouriteEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    image = image
)