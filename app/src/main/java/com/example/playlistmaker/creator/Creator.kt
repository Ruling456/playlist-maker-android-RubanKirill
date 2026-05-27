package com.example.playlistmaker.creator

import com.example.playlistmaker.data.RetrofitNetworkClient
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.domain.TracksRepository

object Creator {
    private fun getStorage(): Storage {
        return Storage()
    }

    private fun getNetworkClient(): NetworkClient {
        return RetrofitNetworkClient(getStorage())
    }

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(getNetworkClient())
    }
}
