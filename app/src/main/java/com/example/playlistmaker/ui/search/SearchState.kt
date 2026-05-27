package com.example.playlistmaker.ui.search

import com.example.playlistmaker.domain.Track

sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    data class Success(val foundList: List<Track>) : SearchState()
    data class Error(val error: String) : SearchState()
}
