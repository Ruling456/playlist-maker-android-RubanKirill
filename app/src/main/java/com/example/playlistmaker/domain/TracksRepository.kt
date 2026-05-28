package com.example.playlistmaker.domain

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
    suspend fun getTrackById(id: String): Track?
    fun getTrackByNameAndArtist(track: Track): kotlinx.coroutines.flow.Flow<Track?>
    fun getFavoriteTracks(): kotlinx.coroutines.flow.Flow<List<Track>>
    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun deleteTrackFromPlaylist(track: Track)
    suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean)
    fun deleteTracksByPlaylistId(playlistId: Long)
}
