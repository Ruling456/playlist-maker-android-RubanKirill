package com.example.playlistmaker.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.Playlist
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksRepository: TracksRepository,
    private val playlistId: Long
) : ViewModel() {

    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist: StateFlow<Playlist?> = _playlist.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.getPlaylist(playlistId).collect { _playlist.value = it }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTracksByPlaylistId(playlistId)
            playlistsRepository.deletePlaylistById(playlistId)
        }
    }

    fun removeTrackFromPlaylist(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTrackFromPlaylist(track)
            _playlist.update { current ->
                current?.copy(tracks = current.tracks.filter { it.trackId != track.trackId })
            }
        }
    }

    fun updateCover(coverUri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = _playlist.value ?: return@launch
            playlistsRepository.updatePlaylist(current.copy(coverUri = coverUri))
        }
    }
}
