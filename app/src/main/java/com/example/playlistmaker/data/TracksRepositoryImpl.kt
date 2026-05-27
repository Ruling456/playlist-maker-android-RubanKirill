package com.example.playlistmaker.data

import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.delay

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    private val mockedTracks = listOf(
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

    override suspend fun searchTracks(expression: String): List<Track> {
        delay(1000)
        return mockedTracks.filter { track ->
            track.trackName.contains(expression, ignoreCase = true) ||
            track.artistName.contains(expression, ignoreCase = true)
        }
    }
}
