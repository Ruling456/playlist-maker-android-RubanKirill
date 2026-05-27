package com.example.playlistmaker.domain

import com.example.playlistmaker.data.BaseResponse

interface NetworkClient {
    fun doRequest(dto: Any): BaseResponse
}
