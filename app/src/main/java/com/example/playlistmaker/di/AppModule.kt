package com.example.playlistmaker.di

import com.example.playlistmaker.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.RetrofitNetworkClient
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.TracksRepository
import com.example.playlistmaker.ui.playlists.PlaylistDetailViewModel
import com.example.playlistmaker.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.ui.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
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
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().trackDao() }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().searchHistoryDao() }
    single<TracksRepository> { TracksRepositoryImpl(get(), get(), get()) }
    single<PlaylistsRepository> { PlaylistsRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { SearchViewModel(get()) }
    viewModel { PlaylistsViewModel(get(), get()) }
    viewModel { params -> PlaylistDetailViewModel(get(), get(), params.get()) }
}
