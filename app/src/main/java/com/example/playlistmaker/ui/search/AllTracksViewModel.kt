package com.example.playlistmaker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AllTracksViewModel(
    private val tracksRepository: TracksRepository,
) : ViewModel() {

    private val _allTracksScreenState = MutableStateFlow<SearchState>(SearchState.Searching)
    val allTracksScreenState = _allTracksScreenState.asStateFlow()

    fun fetchData(query: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _allTracksScreenState.update { SearchState.Searching }
                val list = tracksRepository.searchTracks(query)
                _allTracksScreenState.update { SearchState.Success(list = list) }
            } catch (e: Exception) {
                _allTracksScreenState.update { SearchState.Fail(e.message ?: "Unknown error") }
            }
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AllTracksViewModel(Creator.getTracksRepository()) as T
                }
            }
    }
}
