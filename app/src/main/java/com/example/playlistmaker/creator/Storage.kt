package com.example.playlistmaker.creator

import com.example.playlistmaker.data.TrackDto

class Storage {
    private val listTracks = listOf(
        TrackDto(
            trackId = "1",
            trackName = "Владивосток 2000",
            artistName = "Мумий Тролль",
            trackTimeMillis = 158000,
            artworkUrl100 = ""),
        TrackDto(
            trackId = "2",
            trackName = "Группа крови",
            artistName = "Кино",
            trackTimeMillis = 283000, // 4:43
            artworkUrl100 = ""
        ),
        TrackDto(
            trackId = "3",
            trackName = "Не смотри назад",
            artistName = "Ария",
            trackTimeMillis = 312000, // 5:12
            artworkUrl100 = ""
        ),
        TrackDto(
            trackId = "4",
            trackName = "Звезда по имени Солнце",
            artistName = "Кино",
            trackTimeMillis = 225000,
            artworkUrl100 = ""
        ),
        TrackDto(
            trackId = "5",
            trackName = "Лондон",
            artistName = "Аквариум",
            trackTimeMillis = 272000,
            artworkUrl100 = ""
        ),
        TrackDto(
            trackId = "6",
            trackName = "На заре",
            artistName = "Альянс",
            trackTimeMillis = 230000,
            artworkUrl100 = ""
        ),
        TrackDto(
            trackId = "7",
            trackName = "Перемен",
            artistName = "Кино",
            trackTimeMillis = 296000,
            artworkUrl100 = ""
        ),
        TrackDto(
            trackId = "8",
            trackName = "Розовый фламинго",
            artistName = "Сплин",
            trackTimeMillis = 195000,
            artworkUrl100 = ""
        ),
        TrackDto(
            trackId = "9",
            trackName = "Танцевать",
            artistName = "Мельница",
            trackTimeMillis = 222000,
            artworkUrl100 = ""
        ),
        TrackDto(
            trackId = "10",
            trackName = "Чёрный бумер",
            artistName = "Серёга",
            trackTimeMillis = 241000,
            artworkUrl100 = ""
        )
    )

    fun search(request: String): List<TrackDto> {
        val result = listTracks.filter {
            it.trackName
                .lowercase()
                .contains(request.lowercase())  ||
                    it.artistName.lowercase().contains(request.lowercase())
        }
        return result
    }
}
