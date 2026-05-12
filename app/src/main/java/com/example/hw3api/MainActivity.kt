package com.example.hw3api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.hw3api.ui.CharacterViewModel
import com.example.hw3api.ui.screens.CharacterDetailScreen
import com.example.hw3api.ui.screens.CharacterListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val viewModel: CharacterViewModel = viewModel()

            NavHost(navController, startDestination = "list") {
                composable("list") {
                    LaunchedEffect(Unit) {
                        viewModel.loadInitial()
                    }

                    CharacterListScreen(
                        uiState = viewModel.listState,
                        searchQuery = viewModel.listState.searchQuery,
                        onSearchChange = viewModel::onSearchChange,
                        onRetry = viewModel::retry,
                        onLoadMore = viewModel::loadNextPage,
                        onClick = { navController.navigate("detail/$it") },
                        onFavouriteClick = viewModel::onFavouriteClick,
                        favourites = viewModel.listState.favourites
                    )
                }

                composable("detail/{id}") { backStack ->
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