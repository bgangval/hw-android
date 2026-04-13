package com.example.hw3api.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.hw3api.ui.CharacterUiState
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    uiState: CharacterUiState,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onRetry: () -> Unit,
    onClick: (Int) -> Unit,
    onLoadMore: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Rick & Morty",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search") }
            )

            Spacer(Modifier.height(12.dp))

            when (uiState) {

                is CharacterUiState.Loading -> {
                    Text("Loading...")
                }

                is CharacterUiState.Error -> {
                    Column {
                        Text(uiState.message)
                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }

                is CharacterUiState.Empty -> {
                    Text("No results")
                }

                is CharacterUiState.Success -> {

                    LazyColumn {
                        items(
                            uiState.characters,
                            key = { it.id }
                        ) { character ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable { onClick(character.id) }
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(character.name, fontWeight = FontWeight.Bold)
                                    Text(character.status)
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = onLoadMore,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text("Load more")
                            }
                        }
                    }
                }
            }
        }
    }
}