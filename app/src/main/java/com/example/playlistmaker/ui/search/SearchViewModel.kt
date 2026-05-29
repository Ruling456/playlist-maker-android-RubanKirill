package com.example.playlistmaker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    val searchHistory = tracksRepository.getSearchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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
        viewModelScope.launch { performSearch(whatSearch) }
    }

    fun addToHistory(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.addToSearchHistory(query)
        }
    }

    private suspend fun performSearch(query: String) {
        _searchScreenState.update { SearchState.Searching }
        try {
            val list = tracksRepository.searchTracks(expression = query)
            _searchScreenState.update { SearchState.Success(list = list) }
        } catch (e: IOException) {
            _searchScreenState.update { SearchState.Fail(e.message.toString()) }
        } catch (e: Exception) {
            _searchScreenState.update { SearchState.Fail(e.message ?: "Неизвестная ошибка") }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
