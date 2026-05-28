package com.example.playlistmaker.data

import com.example.playlistmaker.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(
    val trackId: Long = 0L,
    val trackName: String? = null,
    val artistName: String? = null,
    val trackTimeMillis: Long = 0L,
    val artworkUrl100: String? = null
)

fun TrackDto.toTrack() = Track(
    trackId = trackId.toString(),
    trackName = trackName.orEmpty(),
    artistName = artistName.orEmpty(),
    trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis),
    artworkUrl100 = artworkUrl100.orEmpty()
)
