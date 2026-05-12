package com.example.hw3api.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3api.data.CharacterRepository
import com.example.hw3api.model.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val endReached: Boolean = false,
    val paginationError: Boolean = false,
    val searchQuery: String = "",
    val favourites: List<Character> = emptyList(),
    val favouriteLoadingIds: Set<Int> = emptySet()
)

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val character: Character) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    var listState by mutableStateOf(ListUiState(isLoading = true))
        private set

    var detailState by mutableStateOf<DetailUiState>(DetailUiState.Loading)
        private set

    private var currentPage = 1
    private var endReached = false
    private var characters = listOf<Character>()

    private var requestId = 0
    private var searchJob: Job? = null

    private var favouriteIds: Set<Int> = emptySet()

    fun loadInitial() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            currentPage = 1
            endReached = false
            characters = emptyList()
            loadFavouriteIds()
            loadCharacters(loadMore = false)
        }
    }

    fun onSearchChange(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            currentPage = 1
            endReached = false
            characters = emptyList()

            listState = listState.copy(
                searchQuery = query,
                isLoading = true,
                errorMessage = null
            )

            loadCharacters(loadMore = false)
        }
    }

    private suspend fun loadFavouriteIds() {
        try {
            favouriteIds = repository.getFavouritesIds()
            val favList = repository.getFavourites()
            listState = listState.copy(favourites = favList)
        } catch (e: Exception) {
            listState = listState.copy(
                errorMessage = "Failed to load favourites: ${e.message}"
            )
        }
    }

    private suspend fun loadCharacters(loadMore: Boolean = false) {
        if (listState.isLoading && loadMore) return
        if (!loadMore && endReached) return

        val currentRequest = ++requestId
        val query = listState.searchQuery

        if (!loadMore) {
            listState = listState.copy(isLoading = true, errorMessage = null)
        }

        try {
            val result = repository.searchCharacters(query, currentPage)
            if (currentRequest != requestId) return

            if (result.isEmpty() && !loadMore) {
                endReached = true
                listState = listState.copy(
                    isLoading = false,
                    characters = emptyList(),
                    endReached = true
                )
                return
            }

            characters = if (loadMore) characters + result else result
            endReached = result.isEmpty()

            listState = listState.copy(
                isLoading = false,
                characters = characters,
                endReached = endReached,
                paginationError = false
            )
            currentPage++

        } catch (e: Exception) {
            if (currentRequest != requestId) return
            if (!loadMore) {
                listState = listState.copy(
                    isLoading = false,
                    errorMessage = "Loading error: ${e.message}"
                )
            } else {
                listState = listState.copy(paginationError = true)
            }
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            if (!listState.isLoading && !listState.endReached && !listState.paginationError) {
                loadCharacters(true)
            }
        }
    }

    private var detailRequestId = 0

    fun loadCharacter(id: Int) {
        viewModelScope.launch {
            val currentRequest = ++detailRequestId
            detailState = DetailUiState.Loading

            try {
                val character = repository.getCharacter(id)
                if (currentRequest != detailRequestId) return@launch
                detailState = DetailUiState.Success(character)
            } catch (e: Exception) {
                if (currentRequest != detailRequestId) return@launch
                detailState = DetailUiState.Error("Loading error: ${e.message}")
            }
        }
    }

    fun onFavouriteClick(character: Character) {
        if (character.id in listState.favouriteLoadingIds) return

        viewModelScope.launch {
            try {
                listState = listState.copy(
                    favouriteLoadingIds = listState.favouriteLoadingIds + character.id
                )

                val isCurrentlyFavourite = character.id in favouriteIds
                repository.toggleFavourite(character.copy(isFavourite = isCurrentlyFavourite))
                loadFavouriteIds()

                characters = characters.map {
                    if (it.id == character.id) it.copy(isFavourite = !isCurrentlyFavourite) else it
                }
                listState = listState.copy(characters = characters)

                val det = detailState
                if (det is DetailUiState.Success && det.character.id == character.id) {
                    detailState = DetailUiState.Success(
                        det.character.copy(isFavourite = !isCurrentlyFavourite)
                    )
                }
            } catch (e: Exception) {
                listState = listState.copy(
                    errorMessage = "Failed to update favourite: ${e.message}"
                )
            } finally {
                listState = listState.copy(
                    favouriteLoadingIds = listState.favouriteLoadingIds - character.id
                )
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
            loadFavouriteIds()
            loadCharacters(loadMore = false)
        }
    }
}