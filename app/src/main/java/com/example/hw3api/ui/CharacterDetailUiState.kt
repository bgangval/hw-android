package com.example.hw3api.ui

import com.example.hw3api.model.Character

sealed class CharacterDetailUiState {

    object Loading : CharacterDetailUiState()

    data class Success(
        val character: Character
    ) : CharacterDetailUiState()

    data class Error(
        val message: String
    ) : CharacterDetailUiState()
}