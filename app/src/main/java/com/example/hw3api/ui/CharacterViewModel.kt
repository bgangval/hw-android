package com.example.hw3api.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3api.data.CharacterRepository
import com.example.hw3api.model.Character
import kotlinx.coroutines.launch

class CharacterViewModel : ViewModel() {

    private val repository = CharacterRepository()

    var uiState by mutableStateOf<CharacterUiState>(CharacterUiState.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private var currentPage = 1
    private var isLoading = false
    private var endReached = false

    private var characters = listOf<Character>()

    init {
        loadCharacters()
    }

    fun onSearchChange(query: String) {
        searchQuery = query

        currentPage = 1
        endReached = false
        characters = emptyList()

        loadCharacters()
    }

    fun loadCharacters(loadMore: Boolean = false) {
        if (isLoading || endReached) return

        viewModelScope.launch {

            isLoading = true

            try {
                val result = repository.searchCharacters(
                    searchQuery,
                    currentPage
                )

                characters = if (loadMore) {
                    characters + result
                } else {
                    result
                }

                endReached = result.isEmpty()

                uiState = if (characters.isEmpty()) {
                    CharacterUiState.Empty
                } else {
                    CharacterUiState.Success(characters)
                }

                currentPage++

            } catch (e: Exception) {
                uiState = CharacterUiState.Error("Loading error")
            } finally {
                isLoading = false
            }
        }
    }

    fun loadNextPage() {
        loadCharacters(loadMore = true)
    }

    suspend fun getCharacter(id: Int): Character {
        return repository.getCharacter(id)
    }
}