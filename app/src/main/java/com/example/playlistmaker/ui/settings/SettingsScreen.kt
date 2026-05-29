package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R

sealed class SettingsItemType {
    object Switch : SettingsItemType()
    data class IconButton(val icon: ImageVector) : SettingsItemType()
    object Arrow : SettingsItemType()
}

data class SettingsMenuItem(
    val id: String,
    val titleRes: Int,
    val type: SettingsItemType
)

private val settingsItems = listOf(
    SettingsMenuItem("dark_theme", R.string.settings_dark_theme, SettingsItemType.Switch),
    SettingsMenuItem("share_app", R.string.settings_share_app, SettingsItemType.IconButton(Icons.Default.Share)),
    SettingsMenuItem("support", R.string.settings_support, SettingsItemType.IconButton(Icons.Default.HeadsetMic)),
    SettingsMenuItem("user_agreement", R.string.settings_user_agreement, SettingsItemType.Arrow)
)

@Composable
fun SettingsScreen(onBackPressed: () -> Unit) {
    val context = LocalContext.current
    var isDarkTheme by remember { mutableStateOf(false) }

    val shareMessage = stringResource(R.string.share_app_message)
    val supportEmail = stringResource(R.string.support_email)
    val supportSubject = stringResource(R.string.support_email_subject)
    val supportBody = stringResource(R.string.support_email_body)
    val agreementUrl = stringResource(R.string.user_agreement_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
            )
        }

        Spacer(Modifier.height(24.dp))

        LazyColumn {
            items(settingsItems) { item ->
                SettingsRow(
                    item = item,
                    isDarkTheme = isDarkTheme,
                    onThemeChanged = { isDarkTheme = it },
                    onClick = {
                        when (item.id) {
                            "share_app" -> {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                                }
                                context.startActivity(Intent.createChooser(intent, null))
                            }
                            "support" -> {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
                                    putExtra(Intent.EXTRA_SUBJECT, supportSubject)
                                    putExtra(Intent.EXTRA_TEXT, supportBody)
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    context.startActivity(Intent.createChooser(intent, null))
                                }
                            }
                            "user_agreement" -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
                                context.startActivity(intent)
                            }
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SettingsRow(
    item: SettingsMenuItem,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(enabled = item.type !is SettingsItemType.Switch, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(item.titleRes),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        when (item.type) {
            is SettingsItemType.Switch -> {
                Switch(checked = isDarkTheme, onCheckedChange = onThemeChanged)
            }
            is SettingsItemType.IconButton -> {
                Icon(imageVector = item.type.icon, contentDescription = null, tint = Color.Gray)
            }
            is SettingsItemType.Arrow -> {
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}
