package com.example.playlistmaker.data

import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class TracksRepositoryImpl(private val database: DatabaseMock) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        delay(1000)
        return database.searchTracks(expression)
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return database.getTrackByNameAndArtist(track)
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        database.insertTrack(track.copy(playlistId = playlistId))
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        database.insertTrack(track.copy(playlistId = 0))
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        database.insertTrack(track.copy(favorite = isFavorite))
    }

    override fun deleteTracksByPlaylistId(playlistId: Long) {
        database.deleteTracksByPlaylistId(playlistId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.getFavoriteTracks()
    }
}
