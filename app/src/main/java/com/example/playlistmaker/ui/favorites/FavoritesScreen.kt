package com.example.playlistmaker.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.ui.search.TrackItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onNavigateToTrack: (String) -> Unit = {},
    playlistsViewModel: PlaylistsViewModel = koinViewModel()
) {
    val favoritesList by playlistsViewModel.favoriteList.collectAsState(initial = emptyList())
    var trackToRemove by remember { mutableStateOf<Track?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.favourites),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (favoritesList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.no_favorite_tracks), fontSize = 16.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(favoritesList, key = { it.trackId }) { track ->
                    TrackItem(
                        track = track,
                        onLongClick = { trackToRemove = track },
                        onClick = { onNavigateToTrack(track.trackId) }
                    )
                }
            }
        }
    }

    trackToRemove?.let { track ->
        AlertDialog(
            modifier = Modifier
                .width(280.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            onDismissRequest = { trackToRemove = null },
            text = {
                Text(
                    text = stringResource(R.string.remove_from_favorites_confirm, track.trackName),
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    playlistsViewModel.toggleFavorite(track, false)
                    trackToRemove = null
                }) {
                    Text(stringResource(R.string.yes), color = Color(0xFF3772E7), fontSize = 14.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { trackToRemove = null }) {
                    Text(stringResource(R.string.no), color = Color(0xFF3772E7), fontSize = 14.sp)
                }
            },
            containerColor = Color.White
        )
    }
}
