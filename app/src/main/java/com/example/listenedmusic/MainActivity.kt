package com.example.listenedmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.collections.filter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MusicApp()
                }
            }
        }
    }
}

enum class Status {
    Planned,
    Listening,
    Done
}

data class Music(
    val id: Int,
    val title: String,
    val artist: String,
    val year: Int,
    val genre: String,
    val status: Status = Status.Planned
)

val sampleMusicList = listOf(
    Music(1, "Blinding Lights", "The Weeknd", 2019, "Pop"),
    Music(2, "Bohemian Rhapsody", "Queen", 1975, "Rock"),
    Music(3, "Smells Like Teen Spirit", "Nirvana", 1991, "Grunge"),
    Music(4, "Billie Jean", "Michael Jackson", 1982, "Pop"),
    Music(5, "Lose Yourself", "Eminem", 2002, "Hip-Hop"),
    Music(6, "Shape of You", "Ed Sheeran", 2017, "Pop"),
    Music(7, "Hotel California", "Eagles", 1976, "Rock"),
    Music(8, "Bad Guy", "Billie Eilish", 2019, "Electropop")
)

class MusicStateHolder(
    music: List<Music>
) {

    var searchQuery by mutableStateOf("")
    var filter by mutableStateOf<Status?>(null)

    var musicList by mutableStateOf(music)

    val filteredMusic: List<Music>
        get() = musicList
            .filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
            .filter {
                filter == null || it.status == filter
            }

    val totalCount get() = musicList.size
    val doneCount get() = musicList.count { it.status == Status.Done }
    val progressCount get() = musicList.count { it.status == Status.Listening }

    fun onSearchChange(value: String) {
        searchQuery = value
    }

    fun onFilterChange(status: Status?) {
        filter = status
    }

    fun nextStatus(id: Int) {
        musicList = musicList.map {
            if (it.id == id) {
                val newStatus = when (it.status) {
                    Status.Planned -> Status.Listening
                    Status.Listening -> Status.Done
                    Status.Done -> Status.Planned
                }
                it.copy(status = newStatus)
            } else it
        }
    }
}

@Composable
fun MusicApp() {
    val stateHolder = remember {
        MusicStateHolder(sampleMusicList)
    }

    MusicListScreen(
        musicList = stateHolder.filteredMusic,
        searchQuery = stateHolder.searchQuery,
        onSearchChange = stateHolder::onSearchChange,
        onNextStatus = stateHolder::nextStatus,
        onFilterChange = stateHolder::onFilterChange,
        total = stateHolder.totalCount,
        done = stateHolder.doneCount,
        progress = stateHolder.progressCount
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicListScreen(
    musicList: List<Music>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onNextStatus: (Int) -> Unit,
    onFilterChange: (Status?) -> Unit,
    total: Int,
    done: Int,
    progress: Int
) {

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Music Watchlist") })
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Text("Total: $total  |  In Progress: $progress  |  Done: $done")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search by title") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                Button(onClick = { onFilterChange(null) }) {
                    Text("All")
                }

                Button(onClick = { onFilterChange(Status.Planned) }) {
                    Text("Planned")
                }

                Button(onClick = { onFilterChange(Status.Listening) }) {
                    Text("Listening")
                }

                Button(onClick = { onFilterChange(Status.Done) }) {
                    Text("Done")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (musicList.isEmpty()) {
                Text("Nothing found")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(musicList, key = { it.id }) { item ->
                        MusicCard(
                            music = item,
                            onNextStatus = { onNextStatus(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MusicCard(
    music: Music,
    onNextStatus: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Text(music.title, fontWeight = FontWeight.Bold)
        Text("${music.artist}")
        Text("${music.year} - ${music.genre}")
        Text("Status: ${music.status}")

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onNextStatus) {
            Text("Next status")
        }
    }
}