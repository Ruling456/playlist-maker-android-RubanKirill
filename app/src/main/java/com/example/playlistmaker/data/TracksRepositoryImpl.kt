package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.SearchHistoryEntity
import com.example.playlistmaker.data.db.SearchHistoryDao
import com.example.playlistmaker.data.db.TrackDao
import com.example.playlistmaker.data.db.toEntity
import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksRepositoryImpl(
    private val trackDao: TrackDao,
    private val searchHistoryDao: SearchHistoryDao,
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
        return trackDao.getTrackById(id)?.toDomain() ?: searchCache[id]
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return trackDao.getTrackByNameAndArtist(track.trackName, track.artistName)
            .map { it?.toDomain() }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        trackDao.upsertTrack(track.copy(playlistId = playlistId).toEntity())
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        trackDao.removeFromPlaylist(track.trackId)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        trackDao.upsertTrack(track.copy(favorite = isFavorite).toEntity())
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        trackDao.deleteNonFavoriteTracksByPlaylistId(playlistId)
        trackDao.clearPlaylistIdForFavoriteTracks(playlistId)
    }

    override suspend fun addToSearchHistory(query: String) {
        searchHistoryDao.deleteByQuery(query)
        searchHistoryDao.insert(SearchHistoryEntity(query = query))
    }

    override fun getSearchHistory(): Flow<List<String>> {
        return searchHistoryDao.getRecentHistory().map { list -> list.map { it.query } }
    }
}
