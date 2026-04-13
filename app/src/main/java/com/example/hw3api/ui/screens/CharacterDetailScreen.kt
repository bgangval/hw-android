package com.example.hw3api.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.hw3api.ui.CharacterDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    uiState: CharacterDetailUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Character",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    Button(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when(uiState) {
                is CharacterDetailUiState.Loading -> {
                    Text("Loading...")
                }

                is CharacterDetailUiState.Error -> {
                    Column {
                        Text(uiState.message)
                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }

                is CharacterDetailUiState.Success -> {
                    val character = uiState.character

                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {

                        Column(modifier = Modifier.padding(16.dp)) {

                            Text("Status", fontWeight = FontWeight.Bold)
                            Text(character.status)

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Species", fontWeight = FontWeight.Bold)
                            Text(character.species)
                        }
                    }
                }
            }
        }
    }
}