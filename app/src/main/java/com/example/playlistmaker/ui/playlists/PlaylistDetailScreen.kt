package com.example.playlistmaker.ui.playlists

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Playlist
import com.example.playlistmaker.domain.Track
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File

@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    onBackPressed: () -> Unit,
    onNavigateToTrack: (String) -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    val viewModel: PlaylistDetailViewModel = koinViewModel { parametersOf(playlistId) }
    val playlistState by viewModel.playlist.collectAsState()
    val context = LocalContext.current

    val coverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = File(context.filesDir, "cover_$playlistId.jpg")
            context.contentResolver.openInputStream(it)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            viewModel.updateCover(file.absolutePath)
        }
    }

    PlaylistDetailScreenContent(
        playlist = playlistState,
        onBackPressed = onBackPressed,
        onClickDelete = { viewModel.deletePlaylist() },
        onNavigateToTrack = onNavigateToTrack,
        onNavigateToEdit = { onNavigateToEdit(playlistId) },
        onRemoveTrackFromPlaylist = { track -> viewModel.removeTrackFromPlaylist(track) },
        onPickCover = { coverLauncher.launch("image/*") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreenContent(
    playlist: Playlist?,
    onBackPressed: () -> Unit,
    onClickDelete: () -> Unit,
    onNavigateToTrack: (String) -> Unit = {},
    onNavigateToEdit: () -> Unit = {},
    onRemoveTrackFromPlaylist: (Track) -> Unit = {},
    onPickCover: () -> Unit = {}
) {
    if (playlist == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val durationMin = playlist.tracks.sumOf { track ->
        val parts = track.trackTime.split(":")
        if (parts.size == 2) parts[0].toIntOrNull() ?: 0 else 0
    }

    var showMenuBottomSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var trackToRemove by remember { mutableStateOf<Track?>(null) }

    val context = LocalContext.current

    fun sharePlaylist() {
        val trackList = playlist.tracks.mapIndexed { i, t ->
            "${i + 1}. ${t.trackName} - ${t.artistName}"
        }.joinToString("\n")
        val text = "${playlist.name}\n\n$trackList"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    val scaffoldState = rememberBottomSheetScaffoldState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        val coverHeight = maxWidth
        val topContentHeight = 56.dp + 160.dp
        val peekHeight = (maxHeight - coverHeight - topContentHeight).coerceAtLeast(96.dp)

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = peekHeight,
            sheetDragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFDEDEDE))
                    )
                }
            },
            sheetContent = {
                if (playlist.tracks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.playlist_empty),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(playlist.tracks, key = { it.trackId }) { track ->
                            PlaylistItemTrack(
                                track = track,
                                onClick = { onNavigateToTrack(track.trackId) },
                                onLongPress = { trackToRemove = track }
                            )
                        }
                    }
                }
            },
            sheetContainerColor = Color.White,
            containerColor = Color.White,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp)
                        .clickable { onBackPressed() }
                )

                // Обложка с кнопкой смены
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    if (!playlist.coverUri.isNullOrBlank()) {
                        AsyncImage(
                            model = playlist.coverUri,
                            contentDescription = stringResource(R.string.playlist_cover_description),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFE6E8EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color(0xFF8B8F92)
                            )
                        }
                    }

                    // Кнопка редактирования обложки
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { onPickCover() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.change_cover),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = playlist.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (playlist.description.isNotBlank()) {
                        Text(text = playlist.description, fontSize = 16.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = stringResource(R.string.playlist_duration_tracks, durationMin, playlist.tracks.size),
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.menu),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { showMenuBottomSheet = true },
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        // Нижнее меню (три точки)
        if (showMenuBottomSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showMenuBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(modifier = Modifier.padding(bottom = 32.dp)) {
                    Text(
                        text = stringResource(R.string.share_playlist),
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showMenuBottomSheet = false
                                sharePlaylist()
                            }
                            .padding(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.edit_playlist_info),
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showMenuBottomSheet = false
                                onNavigateToEdit()
                            }
                            .padding(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.delete_playlist),
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showMenuBottomSheet = false
                                showDeleteDialog = true
                            }
                            .padding(16.dp)
                    )
                }
            }
        }

        // Диалог удаления плейлиста
        if (showDeleteDialog) {
            AlertDialog(
                modifier = Modifier
                    .width(280.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
                onDismissRequest = { showDeleteDialog = false },
                text = {
                    Text(
                        text = stringResource(R.string.delete_playlist_question, playlist.name),
                        color = Color.Black
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        onClickDelete()
                        showDeleteDialog = false
                        onBackPressed()
                    }) {
                        Text(stringResource(R.string.yes), color = Color(0xFF3772E7), fontSize = 14.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(R.string.no), color = Color(0xFF3772E7), fontSize = 14.sp)
                    }
                },
                containerColor = Color.White
            )
        }

        // Диалог удаления трека из плейлиста
        trackToRemove?.let { track ->
            AlertDialog(
                modifier = Modifier
                    .width(280.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
                onDismissRequest = { trackToRemove = null },
                text = {
                    Text(
                        text = stringResource(R.string.remove_from_playlist_question),
                        color = Color.Black
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        onRemoveTrackFromPlaylist(track)
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
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistItemTrack(
    track: Track,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongPress() }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE6E8EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF8B8F92)
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE6E8EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF8B8F92)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.trackName,
                fontSize = 16.sp,
                maxLines = 1,
                color = Color.Black
            )
            Text(
                text = stringResource(R.string.artist_and_time, track.artistName, track.trackTime),
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}
