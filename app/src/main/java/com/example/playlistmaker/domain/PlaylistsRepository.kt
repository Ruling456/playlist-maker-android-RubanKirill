package com.example.playlistmaker.domain

import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylist(playlistId: Long): Flow<Playlist?>
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addNewPlaylist(name: String, description: String, coverUri: String? = null)
    suspend fun deletePlaylistById(id: Long)
    suspend fun updatePlaylist(playlist: Playlist)
}
