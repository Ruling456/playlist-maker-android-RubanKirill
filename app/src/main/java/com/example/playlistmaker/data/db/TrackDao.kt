package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks WHERE trackId = :id")
    suspend fun getTrackById(id: String): TrackEntity?

    @Query("SELECT * FROM tracks WHERE favorite = 1")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE trackName = :name AND artistName = :artist LIMIT 1")
    fun getTrackByNameAndArtist(name: String, artist: String): Flow<TrackEntity?>

    @Query("DELETE FROM tracks WHERE playlistId = :playlistId AND favorite = 0")
    suspend fun deleteNonFavoriteTracksByPlaylistId(playlistId: Long)

    @Query("UPDATE tracks SET playlistId = 0 WHERE playlistId = :playlistId AND favorite = 1")
    suspend fun clearPlaylistIdForFavoriteTracks(playlistId: Long)

    @Query("UPDATE tracks SET playlistId = 0 WHERE trackId = :trackId")
    suspend fun removeFromPlaylist(trackId: String)
}
