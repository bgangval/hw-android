package com.example.hw3api.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.hw3api.model.Character
import com.example.hw3api.ui.screens.CharacterDetailScreen
import com.example.hw3api.ui.screens.CharacterListScreen
import org.junit.Rule
import org.junit.Test

class NavigationIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `list screen shows loading state`() {
        composeTestRule.setContent {
            CharacterListScreen(
                uiState = ListUiState(isLoading = true),
                searchQuery = "",
                onSearchChange = {},
                onRetry = {},
                onLoadMore = {},
                onClick = {},
                onFavouriteClick = {},
                favourites = emptyList()
            )
        }

        composeTestRule
            .onNodeWithText("Loading...")
            .assertIsDisplayed()
    }

    @Test
    fun `list screen shows error state with retry`() {
        var retryClicked = false
        composeTestRule.setContent {
            CharacterListScreen(
                uiState = ListUiState(errorMessage = "Loading error"),
                searchQuery = "",
                onSearchChange = {},
                onRetry = { retryClicked = true },
                onLoadMore = {},
                onClick = {},
                onFavouriteClick = {},
                favourites = emptyList()
            )
        }

        composeTestRule
            .onNodeWithText("Loading error")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Retry")
            .assertIsDisplayed()
            .performClick()

        assert(retryClicked)
    }

    @Test
    fun `list screen shows characters`() {
        val characters = listOf(
            Character(1, "Rick Sanchez", "Alive", "Human", "url1", false),
            Character(2, "Morty Smith", "Alive", "Human", "url2", true)
        )

        composeTestRule.setContent {
            CharacterListScreen(
                uiState = ListUiState(characters = characters),
                searchQuery = "",
                onSearchChange = {},
                onRetry = {},
                onLoadMore = {},
                onClick = {},
                onFavouriteClick = {},
                favourites = emptyList()
            )
        }

        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Morty Smith")
            .assertIsDisplayed()
    }

    @Test
    fun `detail screen shows character info`() {
        val character = Character(1, "Rick Sanchez", "Alive", "Human", "url1", true)

        composeTestRule.setContent {
            CharacterDetailScreen(
                uiState = DetailUiState.Success(character),
                onRetry = {},
                onBack = {},
                onFavouriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Status")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Alive")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Remove from Favorites")
            .assertIsDisplayed()
    }

    @Test
    fun `detail screen shows loading state`() {
        composeTestRule.setContent {
            CharacterDetailScreen(
                uiState = DetailUiState.Loading,
                onRetry = {},
                onBack = {},
                onFavouriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Loading...")
            .assertIsDisplayed()
    }

    @Test
    fun `detail screen shows error with retry`() {
        var retryClicked = false
        composeTestRule.setContent {
            CharacterDetailScreen(
                uiState = DetailUiState.Error("Loading error"),
                onRetry = { retryClicked = true },
                onBack = {},
                onFavouriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Loading error")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Retry")
            .assertIsDisplayed()
            .performClick()

        assert(retryClicked)
    }
}