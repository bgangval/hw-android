package com.example.hw3api.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3api.data.CharacterRepository
import com.example.hw3api.model.Character
import kotlinx.coroutines.launch
import kotlinx.coroutines.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    var uiState by mutableStateOf<CharacterUiState>(CharacterUiState.Loading)
        private set

    var detailState by mutableStateOf<CharacterDetailUiState>(CharacterDetailUiState.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var favourites by mutableStateOf<List<Character>>(emptyList())
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
            loadFavourites()
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

    private suspend fun loadFavourites() {
        try {
            favourites = repository.getFavourites()
        } catch (e: Exception) {
            favourites = emptyList()
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
                    CharacterUiState.Success(
                        characters = characters,
                        endReached = true
                    )
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

            uiState = CharacterUiState.Success(
                characters = characters,
                endReached = endReached
            )
            currentPage++

        } catch (e: Exception) {
            e.printStackTrace()
            if (!loadMore) {
                uiState = CharacterUiState.Error("Loading error")
            } else {
                uiState = CharacterUiState.Success(
                    characters = characters,
                    endReached = endReached,
                    paginationError = true
                )
            }
        } finally {
            isLoading = false
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            if (isLoading || endReached) return@launch
            loadCharacters(true)
        }
    }

    private var detailRequestId = 0

    fun loadCharacter(id: Int) {
        viewModelScope.launch {
            val currentRequest = ++detailRequestId
            detailState = CharacterDetailUiState.Loading

            try {
                val result = repository.getCharacter(id)
                if (currentRequest != detailRequestId) return@launch
                detailState = CharacterDetailUiState.Success(result)
            } catch (e: Exception) {
                e.printStackTrace()
                if (currentRequest != detailRequestId) return@launch
                detailState = CharacterDetailUiState.Error("Loading error")
            }
        }
    }

    fun onFavouriteClick(character: Character) {
        viewModelScope.launch {
            try {
                val newState = !character.isFavourite
                repository.toggleFavourite(character)

                val updated = characters.map {
                    if (it.id == character.id) it.copy(isFavourite = newState) else it
                }
                characters = updated

                val currentState = uiState
                if (currentState is CharacterUiState.Success) {
                    uiState = currentState.copy(characters = updated)
                }

                val det = detailState
                if (det is CharacterDetailUiState.Success && det.character.id == character.id) {
                    detailState = CharacterDetailUiState.Success(
                        det.character.copy(isFavourite = newState)
                    )
                }

                loadFavourites()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    init {
        loadInitial()

    }

    fun retry() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            currentPage = 1
            endReached = false
            characters = emptyList()
            loadFavourites()
            loadCharacters(loadMore = false)
        }
    }
}