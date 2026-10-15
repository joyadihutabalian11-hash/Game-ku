package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerProfile
import com.example.ui.theme.JoyOrange
import com.example.ui.theme.JoyPurple
import com.example.ui.theme.JoyYellow

@Composable
fun JoyTopBar(
    title: String = "JOYGAMING",
    currentProfile: PlayerProfile?,
    totalStars: Int = 0,
    isSoundEnabled: Boolean = true,
    onSoundToggle: () -> Unit,
    onProfileClick: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    Surface(
        color = JoyPurple,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = JoyYellow,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Stars Count Tag
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = JoyOrange,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Bintang",
                            tint = JoyYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalStars",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Sound FX Toggle Button
                IconButton(
                    onClick = onSoundToggle,
                    modifier = Modifier.testTag("sound_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = if (isSoundEnabled) "Suara Aktif" else "Suara Mati",
                        tint = Color.White
                    )
                }

                // Profile Avatar Button
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onProfileClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("top_bar_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getAvatarEmoji(currentProfile?.avatarIcon),
                        fontSize = 22.sp
                    )
                }
            }
        }
    }
}

fun getAvatarEmoji(avatarIcon: String?): String = when (avatarIcon) {
    "dino_robot" -> "🦖"
    "bunny_star" -> "🐰"
    "wizard_cat" -> "🐱"
    "rocket_dog" -> "🐶"
    else -> "🤖"
}
