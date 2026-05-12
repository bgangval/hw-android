package com.example.hw3api.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hw3api.model.Character
import com.example.hw3api.ui.ListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    uiState: ListUiState,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onRetry: () -> Unit,
    onClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onFavouriteClick: (Character) -> Unit,
    favourites: List<Character>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Rick & Morty", fontWeight = FontWeight.Bold)
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
                label = { Text("Search by name") }
            )

            Spacer(Modifier.height(12.dp))

            uiState.errorMessage?.let { error ->
                Text(error, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
                return@Column
            }

            if (uiState.isLoading) {
                Text("Loading...")
                return@Column
            }

            if (uiState.characters.isEmpty() && favourites.isEmpty()) {
                Text("No results")
                return@Column
            }

            LazyColumn {
                if (searchQuery.isBlank() && favourites.isNotEmpty()) {
                    item {
                        Text(
                            "Favorites",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(favourites, key = { "fav_${it.id}" }) { character ->
                        FavouriteCard(character, onClick, onFavouriteClick)
                    }

                    if (uiState.characters.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "All Characters",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                items(uiState.characters, key = { it.id }) { character ->
                    CharacterCard(character, onClick, onFavouriteClick)
                }

                if (uiState.paginationError) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Error loading more")
                            Button(onClick = onLoadMore) {
                                Text("Retry")
                            }
                        }
                    }
                }

                if (!uiState.endReached && uiState.characters.isNotEmpty()) {
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

@Composable
private fun CharacterCard(
    character: Character,
    onClick: (Int) -> Unit,
    onFavouriteClick: (Character) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(character.id) }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(character.name, fontWeight = FontWeight.Bold)
                Text(character.status)
            }
            IconButton(onClick = { onFavouriteClick(character) }) {
                Text(
                    if (character.isFavourite) "★" else "☆",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun FavouriteCard(
    character: Character,
    onClick: (Int) -> Unit,
    onFavouriteClick: (Character) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(character.id) }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(character.name, fontWeight = FontWeight.Bold)
                Text(character.status)
            }
            IconButton(onClick = { onFavouriteClick(character) }) {
                Text("★", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}