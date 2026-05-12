package com.example.hw3api.ui

import com.example.hw3api.model.Character

sealed class CharacterUiState {
    object Loading : CharacterUiState()

    data class Success(
        val characters: List<Character>,
        val endReached: Boolean,
        val paginationError: Boolean = false
    ) : CharacterUiState()

    object Empty : CharacterUiState()

    data class Error(
        val message: String
    ) : CharacterUiState()
}