package com.example.playlistmaker.data

import com.example.playlistmaker.creator.Storage
import com.example.playlistmaker.domain.NetworkClient

class RetrofitNetworkClient(private val storage: Storage) : NetworkClient {

    override fun doRequest(dto: Any): BaseResponse {
        if (dto is TracksSearchRequest) {
            val searchList = storage.search(dto.expression)
            return TracksSearchResponse(searchList).apply { resultCode = 200 }
        } else {
            return BaseResponse().apply { resultCode = 400 }
        }
    }
}
