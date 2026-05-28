package com.example.playlistmaker.di

import com.example.playlistmaker.data.DatabaseMock
import com.example.playlistmaker.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.RetrofitNetworkClient
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.TracksRepository
import com.example.playlistmaker.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.ui.search.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }
    single<NetworkClient> { RetrofitNetworkClient(get()) }
}

val dataModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single { DatabaseMock(get()) }
    single<TracksRepository> { TracksRepositoryImpl(get(), get()) }
    single<PlaylistsRepository> { PlaylistsRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { SearchViewModel(get()) }
    viewModel { PlaylistsViewModel(get(), get()) }
}
