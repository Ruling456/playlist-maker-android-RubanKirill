package com.example.playlistmaker.creator

import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.domain.TracksRepository
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.data.DatabaseMock
import com.example.playlistmaker.data.PlaylistsRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object Creator {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val databaseMock = DatabaseMock(scope)

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(databaseMock)
    }

    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(databaseMock)
    }
}
