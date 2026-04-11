package com.example.hw3api.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.hw3api.ui.CharacterViewModel
import com.example.hw3api.model.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    id: Int,
    viewModel: CharacterViewModel,
    onBack: () -> Unit
) {
    var character by remember { mutableStateOf<Character?>(null) }

    LaunchedEffect(id) {
        character = viewModel.getCharacter(id)
    }

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

            if (character == null) {
                Text("Loading...")
            } else {

                Text(
                    text = character!!.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = "Status",
                            fontWeight = FontWeight.Bold
                        )
                        Text(character!!.status)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Species",
                            fontWeight = FontWeight.Bold
                        )
                        Text(character!!.species)
                    }
                }
            }
        }
    }
}