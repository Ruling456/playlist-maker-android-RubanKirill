package com.example.playlistmaker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {
    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    private var searchJob: Job? = null

    fun searchDebounce(changedText: String) {
        searchJob?.cancel()
        if (changedText.isBlank()) {
            _searchScreenState.update { SearchState.Initial }
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(changedText)
        }
    }

    fun search(whatSearch: String) {
        searchJob?.cancel()
        if (whatSearch.isBlank()) {
            _searchScreenState.update { SearchState.Initial }
            return
        }
        performSearch(whatSearch)
    }

    private fun performSearch(query: String) {
        _searchScreenState.update { SearchState.Searching }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = tracksRepository.searchTracks(expression = query)
                _searchScreenState.update { SearchState.Success(list = list) }
            } catch (e: IOException) {
                _searchScreenState.update { SearchState.Fail(e.message.toString()) }
            } catch (e: Exception) {
                _searchScreenState.update { SearchState.Fail(e.message ?: "Неизвестная ошибка") }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
