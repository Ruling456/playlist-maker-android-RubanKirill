package com.example.playlistmaker.ui.search

import com.example.playlistmaker.domain.Track

sealed class SearchState {
    object Initial : SearchState()
    object Searching : SearchState()
    data class Success(val list: List<Track>) : SearchState()
    data class Fail(val error: String) : SearchState()
}
