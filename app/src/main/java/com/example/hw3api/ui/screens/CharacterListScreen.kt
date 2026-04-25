package com.example.hw3api.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
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
import com.example.hw3api.model.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    uiState: CharacterUiState,
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
                label = { Text("Search by name") }
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
                    if (favourites.isNotEmpty()) {
                        Text(
                            text = "Favorites",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        LazyColumn {
                            items(favourites, key = { "fav_${it.id}" }) { character ->
                                FavouriteCard(character, onClick, onFavouriteClick)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("No search results")
                    } else {
                        Text("No results")
                    }
                }

                is CharacterUiState.Success -> {

                    LazyColumn {
                        if (searchQuery.isBlank() && favourites.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Favorites",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            items(
                                favourites,
                                key = { "fav_${it.id}" }
                            ) { character ->
                                FavouriteCard(character, onClick, onFavouriteClick)
                            }

                            item {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "All Characters",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

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
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(character.name, fontWeight = FontWeight.Bold)
                                        Text(character.status)
                                    }
                                    IconButton(onClick = { onFavouriteClick(character) }) {
                                        Text(if (character.isFavourite) "★" else "☆")
                                    }
                                }
                            }
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

                        if (!uiState.endReached) {
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
            .clickable { onClick(character.id) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(character.name, fontWeight = FontWeight.Bold)
                Text(character.status)
            }
            IconButton(onClick = { onFavouriteClick(character) }) {
                Text("★")
            }
        }
    }
}