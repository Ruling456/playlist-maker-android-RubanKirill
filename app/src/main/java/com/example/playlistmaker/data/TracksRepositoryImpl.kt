package com.example.playlistmaker.data

import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.flow.Flow

class TracksRepositoryImpl(
    private val database: DatabaseMock,
    private val networkClient: NetworkClient
) : TracksRepository {

    private val searchCache = mutableMapOf<String, Track>()

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response is TracksSearchResponse && response.resultCode == 200) {
            val tracks = response.results.map { it.toTrack() }
            tracks.forEach { searchCache[it.trackId] = it }
            tracks
        } else {
            emptyList()
        }
    }

    override suspend fun getTrackById(id: String): Track? {
        return database.getTrackById(id) ?: searchCache[id]
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
