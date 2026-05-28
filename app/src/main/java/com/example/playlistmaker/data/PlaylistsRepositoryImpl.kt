package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.Playlist
import com.example.playlistmaker.domain.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao
) : PlaylistsRepository {

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistWithTracksById(playlistId).map { it?.toDomain() }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylistsWithTracks().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addNewPlaylist(name: String, description: String, coverUri: String?) {
        playlistDao.insertPlaylist(
            PlaylistEntity(name = name, description = description, coverUri = coverUri)
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        playlistDao.deletePlaylistById(id)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(
            PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                coverUri = playlist.coverUri
            )
        )
    }
}
