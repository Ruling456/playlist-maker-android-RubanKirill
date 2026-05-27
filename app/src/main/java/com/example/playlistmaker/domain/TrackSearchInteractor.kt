package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.Track

interface TrackSearchInteractor {
    suspend fun searchTracks(expression: String): List<Track>
}
