package com.example.playlistmaker.data.db

import androidx.room.Embedded
import androidx.room.Relation
import com.example.playlistmaker.domain.Playlist

data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistEntity,
    @Relation(parentColumn = "id", entityColumn = "playlistId", entity = TrackEntity::class)
    val tracks: List<TrackEntity>
) {
    fun toDomain() = Playlist(
        id = playlist.id,
        name = playlist.name,
        description = playlist.description,
        tracks = tracks.map { it.toDomain() },
        coverUri = playlist.coverUri
    )
}
