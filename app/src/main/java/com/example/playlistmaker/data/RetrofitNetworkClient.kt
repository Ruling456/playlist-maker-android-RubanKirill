package com.example.playlistmaker.data

import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.domain.NetworkClient

class RetrofitNetworkClient(private val itunesApi: ItunesApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): BaseResponse {
        if (dto !is TracksSearchRequest) {
            return BaseResponse().apply { resultCode = 400 }
        }
        return try {
            itunesApi.search(dto.expression).apply { resultCode = 200 }
        } catch (e: Exception) {
            BaseResponse().apply { resultCode = 500 }
        }
    }
}
