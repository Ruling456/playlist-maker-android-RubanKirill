package com.example.playlistmaker.ui.activity

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

@Composable
fun MenuRow(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
                Spacer(Modifier.width(12.dp))
                Text(text = text, color = Color.Black, fontSize = 22.sp)
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit
){
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3772E7))
    ) {
        Text(
            text = stringResource(R.string.app_name),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(start = 16.dp, top = 14.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(top = 8.dp)
        ) {
            MenuRow(
                text = stringResource(R.string.search),
                icon = Icons.Default.Search,
                onClick = onNavigateToSearch
            )

            MenuRow(
                text = stringResource(R.string.playlists),
                icon = ImageVector.vectorResource(R.drawable.ic_library_music),
                onClick = {
                    Toast.makeText(context, "Нажата кнопка \"Плейлисты\"", Toast.LENGTH_SHORT).show()
                }
            )

            MenuRow(
                text = stringResource(R.string.favourites),
                icon = Icons.Default.FavoriteBorder,
                onClick = {
                    Toast.makeText(context, "Нажата кнопка \"Избранное\"", Toast.LENGTH_SHORT).show()
                }
            )

            MenuRow(
                text = stringResource(R.string.settings),
                icon = Icons.Default.Settings,
                onClick = onNavigateToSettings
            )
        }
    }
}
