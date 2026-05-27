package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.TracksRepository

class TrackSearchInteractorImpl(private val repository: TracksRepository) : TrackSearchInteractor {
    override suspend fun searchTracks(expression: String): List<Track> {
        return repository.searchTracks(expression)
    }
}
