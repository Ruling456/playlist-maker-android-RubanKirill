package com.example.playlistmaker.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackPressed: () -> Unit,
    onNavigateToTrack: (String) -> Unit = {}
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    var searchText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Заголовок (в стиле V6)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.search),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Поле ввода (в стиле V6)
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
                viewModel.searchDebounce(it)
            },
            placeholder = { Text(stringResource(R.string.search), color = Color(0xFF8B8F92)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF8B8F92),
                    modifier = Modifier.size(20.dp).clickable {
                        viewModel.search(searchText)
                    }
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = {
                        searchText = ""
                        viewModel.searchDebounce("")
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFF8B8F92))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE6E8EB),
                unfocusedContainerColor = Color(0xFFE6E8EB),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Blue
            ),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Контент (логика из задания)
        Box(modifier = Modifier.fillMaxSize()) {
            when (screenState) {
                is SearchState.Initial -> {
                    Text(
                        text = "Введите строку для поиска",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF8B8F92)
                    )
                }
                is SearchState.Searching -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF3772E7)
                    )
                }
                is SearchState.Success -> {
                    val tracks = (screenState as SearchState.Success).list
                    if (tracks.isEmpty()) {
                        Text(
                            text = "Ничего не найдено",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn {
                            items(
                                items = tracks,
                                key = { track -> track.trackId }
                            ) { track ->
                                TrackItem(track = track) {
                                    onNavigateToTrack(track.trackId)
                                }
                            }
                        }
                    }
                }
                is SearchState.Fail -> {
                    Text(
                        text = "Ошибка: ${(screenState as SearchState.Fail).error}",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun SearchScreenPreview() {
    PlaylistMakerTheme {
        // ignoring the UI builder error for this
    }
}
