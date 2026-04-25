package com.example.hw3api.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouriteDao {

    @Query("SELECT * FROM favourite_character ORDER BY name")
    suspend fun getFavourites(): List<FavouriteEntity>

    @Query("SELECT id FROM favourite_character")
    suspend fun getFavouritesIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(character: FavouriteEntity)

    @Query("DELETE FROM favourite_character WHERE id = :id")
    suspend fun deleteById(id: Int)
}