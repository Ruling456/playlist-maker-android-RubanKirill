package com.example.playlistmaker.domain

data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
)
