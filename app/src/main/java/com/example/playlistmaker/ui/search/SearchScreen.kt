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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackPressed: () -> Unit,
    onNavigateToTrack: (String) -> Unit = {}
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    var searchText by rememberSaveable { mutableStateOf("") }
    var isFieldFocused by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp)
        ) {
            // Заголовок
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.search),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Поле поиска с историей в карточке
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE6E8EB))
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { text ->
                        searchText = text
                        viewModel.searchDebounce(text)
                    },
                    placeholder = {
                        Text(stringResource(R.string.search), color = Color(0xFF8B8F92))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF8B8F92),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { viewModel.search(searchText) }
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = {
                                searchText = ""
                                viewModel.searchDebounce("")
                            }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = Color(0xFF8B8F92)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE6E8EB),
                        unfocusedContainerColor = Color(0xFFE6E8EB),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Blue
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .onFocusChanged { isFieldFocused = it.isFocused }
                )

                if (isFieldFocused && searchText.isEmpty() && searchHistory.isNotEmpty()) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SearchHistoryDropdown(
                        historyList = searchHistory,
                        onItemClicked = { query ->
                            searchText = query
                            viewModel.search(query)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Результаты поиска
            Box(modifier = Modifier.fillMaxSize()) {
                when (screenState) {
                    is SearchState.Initial -> {
                        Text(
                            text = stringResource(R.string.search_empty_hint),
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
                                text = stringResource(R.string.nothing_found),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn {
                                items(
                                    items = tracks,
                                    key = { track -> track.trackId }
                                ) { track ->
                                    TrackItem(track = track) {
                                        viewModel.addToHistory(searchText)
                                        onNavigateToTrack(track.trackId)
                                    }
                                }
                            }
                        }
                    }

                    is SearchState.Fail -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.connection_problem),
                                color = Color.Black,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.load_failed),
                                color = Color(0xFF8B8F92),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            androidx.compose.material3.Button(
                                onClick = { viewModel.search(searchText) }
                            ) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchHistoryDropdown(
    historyList: List<String>,
    onItemClicked: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp)
            .padding(bottom = 8.dp)
    ) {
        items(historyList) { query ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClicked(query) }
                    .padding(vertical = 11.dp, horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF8B8F92)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = query, color = Color.Black)
            }
        }
    }
}
