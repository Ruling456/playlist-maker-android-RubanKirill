package com.example.playlistmaker.data

import com.example.playlistmaker.domain.Playlist
import com.example.playlistmaker.domain.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class DatabaseMock(
    private val scope: CoroutineScope,
) {
    private val historyList = mutableListOf<String>()
    private val _historyUpdates = MutableSharedFlow<Unit>()
    private val playlists = mutableListOf<Playlist>()
    private val tracks = mutableListOf<Track>()

    fun getHistory(): List<String> {
        return historyList.toList()
    }

    fun addToHistory(word: String) {
        historyList.add(word)
        notifyHistoryChanged()
    }

    private fun notifyHistoryChanged() {
        scope.launch(Dispatchers.IO) {
            _historyUpdates.emit(Unit)
        }
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        delay(500)
        val filteredPlaylists = mutableListOf<Playlist>()
        playlists.forEach { playlist ->
            val playlistTracks = tracks.filter { track ->
                track.playlistId == playlist.id
            }
            filteredPlaylists.add(playlist.copy(tracks = playlistTracks))
        }

        emit(filteredPlaylists.toList())
        delay(100)
    }

    fun getPlaylist(id: Long): Flow<Playlist?> = flow {
        emit(playlists.find { it.id == id })
    }

    fun addNewPlaylist(name: String, description: String) {
        playlists.add(
            Playlist(
                id = playlists.size.toLong() + 1,
                name = name,
                description = description,
                tracks = emptyList()
            )
        )
    }

    fun deletePlaylistById(playlistId: Long) {
        playlists.removeIf { it.id == playlistId }
    }

    fun deleteTrackFromPlaylist(trackId: String) {
        tracks.removeIf { it.trackId == trackId }
    }

    fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flow {
        emit(tracks.find { it.trackName == track.trackName && it.artistName == track.artistName })
    }

    fun insertTrack(track: Track) {
        tracks.removeIf { it.trackId == track.trackId }
        tracks.add(track)
    }

    fun getFavoriteTracks(): Flow<List<Track>> = flow {
        delay(300)
        val favorites = tracks.filter { it.favorite }
        emit(favorites)
    }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        tracks.removeIf { it.playlistId == playlistId }
    }

    fun getTrackById(id: String): Track? = tracks.find { it.trackId == id }

    fun searchTracks(expression: String): List<Track> {
        val allTrackIdsInDb = tracks.map { it.trackId }
        val mockedTracks = listOf(
            Track("0", "Владивосток 2000", "Мумий Тролль", "2:38", ""),
            Track("1", "Группа крови", "Кино", "4:43", ""),
            Track("52", "Не смотри назад", "Ария", "5:12", ""),
            Track("7", "Звезда по имени Солнце", "Кино", "3:45", ""),
            Track("5", "Лондон", "Аквариум", "4:32", ""),
            Track("775577", "На заре", "Альянс", "3:50", ""),
            Track("5575", "Перемен", "Кино", "4:56", ""),
            Track("7575", "Розовый фламинго", "Сплин", "3:15", ""),
            Track("777", "Танцевать", "Мельница", "3:42", ""),
            Track("555", "Чёрный бумер", "Серёга", "4:01", "")
        )
        // merge mocked with db
        val foundMocked = mockedTracks.filter { track ->
            (track.trackName.contains(expression, ignoreCase = true) ||
            track.artistName.contains(expression, ignoreCase = true)) 
        }
        val result = foundMocked.map { mt ->
            tracks.find { it.trackId == mt.trackId } ?: mt
        }
        return result
    }
}
