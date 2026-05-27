package com.example.playlistmaker.domain

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
}
