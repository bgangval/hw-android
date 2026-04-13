package com.example.hw3api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.hw3api.ui.CharacterViewModel
import com.example.hw3api.ui.screens.CharacterListScreen
import com.example.hw3api.ui.screens.CharacterDetailScreen

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
                        uiState = viewModel.uiState,
                        searchQuery = viewModel.searchQuery,
                        onSearchChange = viewModel::onSearchChange,
                        onRetry = viewModel::retry,
                        onLoadMore = viewModel::loadNextPage,
                        onClick = {
                            navController.navigate("detail/$it")
                        }
                    )
                }

                composable("detail/{id}") { backStack ->
                    val id = backStack.arguments?.getString("id")?.toInt() ?: 0

                    LaunchedEffect(id) {
                        viewModel.loadCharacter(id)
                    }

                    CharacterDetailScreen(
                        uiState = viewModel.detailState,
                        onRetry = { viewModel.loadCharacter(id) },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}