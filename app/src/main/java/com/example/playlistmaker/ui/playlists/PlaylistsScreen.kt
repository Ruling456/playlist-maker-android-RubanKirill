package com.example.playlistmaker.ui.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Playlist
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter

@Composable
fun PlaylistListItem(playlist: Playlist, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = playlist.name,
            colorFilter = ColorFilter.tint(Color.Gray)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(playlist.name, fontSize = 16.sp, color = Color.Black)
            Text("${playlist.tracks.size} tracks", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PlaylistsScreen(
    modifier: Modifier = Modifier,
    playlistsViewModel: PlaylistsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    addNewPlaylist: () -> Unit = {},
    navigateToPlaylist: (Long) -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    val playlists by playlistsViewModel.playlists.collectAsState(emptyList())

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(24.dp)
                        .clickable { navigateBack() }
                )
                Text(
                    text = "Playlists",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.sp
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                onClick = addNewPlaylist,
                containerColor = Color.LightGray,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add playlist",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            if (playlists.isEmpty()) {
                Text(
                    text = "No playlists found",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(playlists.size) { index ->
                        PlaylistListItem(playlist = playlists[index]) {
                            navigateToPlaylist(playlists[index].id)
                        }
                    }
                }
            }
        }
    }
}
