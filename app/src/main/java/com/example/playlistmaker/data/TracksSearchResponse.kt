package com.example.playlistmaker.data

class TracksSearchResponse(
    val resultCount: Int = 0,
    val results: List<TrackDto> = emptyList()
) : BaseResponse()
