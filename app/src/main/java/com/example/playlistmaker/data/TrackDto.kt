package com.example.playlistmaker.data

data class TrackDto(
    val trackId: String = "",
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String = "",
)
