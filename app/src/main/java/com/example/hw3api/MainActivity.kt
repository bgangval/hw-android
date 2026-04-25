package com.example.hw3api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.*
import com.example.hw3api.ui.CharacterViewModel
import com.example.hw3api.ui.screens.CharacterListScreen
import com.example.hw3api.ui.screens.CharacterDetailScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "list") {
                composable("list") {
                    val viewModel: CharacterViewModel = hiltViewModel()

                    CharacterListScreen(
                        uiState = viewModel.uiState,
                        searchQuery = viewModel.searchQuery,
                        onSearchChange = viewModel::onSearchChange,
                        onRetry = viewModel::retry,
                        onLoadMore = viewModel::loadNextPage,
                        onClick = { navController.navigate("detail/$it") },
                        onFavouriteClick = viewModel::onFavouriteClick,
                        favourites = viewModel.favourites
                    )
                }

                composable("detail/{id}") { backStack ->
                    val viewModel: CharacterViewModel = hiltViewModel()

                    val id = backStack.arguments?.getString("id")?.toIntOrNull()

                    if (id == null) {
                        navController.popBackStack()
                        return@composable
                    }

                    LaunchedEffect(id) {
                        viewModel.loadCharacter(id)
                    }

                    CharacterDetailScreen(
                        uiState = viewModel.detailState,
                        onRetry = { viewModel.loadCharacter(id) },
                        onBack = { navController.popBackStack() },
                        onFavouriteClick = viewModel::onFavouriteClick
                    )
                }
            }
        }
    }
}