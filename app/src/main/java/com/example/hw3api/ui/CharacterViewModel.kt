package com.example.hw3api.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3api.data.CharacterRepository
import com.example.hw3api.model.Character
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

class CharacterViewModel : ViewModel() {

    private val repository = CharacterRepository()

    var uiState by mutableStateOf<CharacterUiState>(CharacterUiState.Loading)
        private set

    var detailState by mutableStateOf<CharacterDetailUiState>(CharacterDetailUiState.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private var currentPage = 1
    private var isLoading = false
    private var endReached = false
    private var characters = listOf<Character>()

    private var requestId = 0
    private var searchJob: Job? = null


    fun loadInitial() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            currentPage = 1
            endReached = false
            characters = emptyList()
            loadCharacters(loadMore = false)
        }
    }

    fun onSearchChange(query: String) {
        searchQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            currentPage = 1
            endReached = false
            characters = emptyList()

            loadCharacters(loadMore = false)
        }
    }

    private suspend fun loadCharacters(loadMore: Boolean = false) {
        if (isLoading || endReached) return

        val currentRequest = ++requestId
        isLoading = true

        if (!loadMore) {
            uiState = CharacterUiState.Loading
        }

        try {
            val result = repository.searchCharacters(searchQuery, currentPage)
            if (currentRequest != requestId) return

            if (result.isEmpty()) {
                endReached = true
                uiState = if (loadMore && characters.isNotEmpty()) {
                    CharacterUiState.Success(characters)
                } else {
                    CharacterUiState.Empty
                }
                return
            }

            characters = if (loadMore) {
                characters + result
            } else {
                result
            }

            uiState = CharacterUiState.Success(characters)
            currentPage++

        } catch (_: Exception) {
            if (!loadMore) {
                uiState = CharacterUiState.Error("Loading error")
            }
        } finally {
            isLoading = false
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            if(isLoading || endReached) return@launch
            loadCharacters(true)
        }
    }

    fun loadCharacter(id: Int) {
        viewModelScope.launch {
            detailState = CharacterDetailUiState.Loading
            try {
                val result = repository.getCharacter(id)
                detailState = CharacterDetailUiState.Success(result)
            } catch (_: Exception){
                detailState = CharacterDetailUiState.Error("Loading error")
            }
        }
    }

    fun retry() {
        loadInitial()
    }
}