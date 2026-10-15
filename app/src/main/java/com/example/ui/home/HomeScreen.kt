package com.example.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerProfile
import com.example.ui.components.getAvatarEmoji
import com.example.ui.theme.JoyBlue
import com.example.ui.theme.JoyGreen
import com.example.ui.theme.JoyOrange
import com.example.ui.theme.JoyPink
import com.example.ui.theme.JoyPurple
import com.example.ui.theme.JoyYellow

@Composable
fun HomeScreen(
    currentProfile: PlayerProfile?,
    totalStars: Int,
    completedLevelsCount: Int,
    unclaimedMissionsCount: Int = 0,
    onStartGameClick: () -> Unit,
    onLevelSelectClick: () -> Unit,
    onDailyMissionsClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onBadgesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_bounce")
    val mascotScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5E35B1),
                        Color(0xFF7E57C2),
                        Color(0xFFEDE7F6)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Player Profile Header Strip
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProfileClick() }
                    .testTag("home_profile_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = getAvatarEmoji(currentProfile?.avatarIcon),
                            fontSize = 36.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = currentProfile?.name ?: "Pemain Koding",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = JoyPurple
                            )
                            Text(
                                text = "Progres: $completedLevelsCount / 100 Level Selesai",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars",
                            tint = JoyYellow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalStars",
                            fontWeight = FontWeight.Black,
                            color = JoyOrange,
                            fontSize = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mascot Header & Game Title
            Box(
                modifier = Modifier
                    .size(115.dp)
                    .clip(CircleShape)
                    .background(JoyYellow)
                    .scale(mascotScale)
                    .testTag("home_mascot_logo"),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🤖", fontSize = 68.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "JOYGAMING",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = JoyYellow,
                    textAlign = TextAlign.Center,
                    fontSize = 38.sp
                )
            )
            Text(
                text = "Game Koding Ceria untuk Anak Pintar! 🚀",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Main Primary Play Action Card
            HomeActionCard(
                title = "MAIN SEKARANG",
                subtitle = "Lanjutkan Petualangan Koding Level ${completedLevelsCount + 1}",
                backgroundColor = JoyGreen,
                icon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Main",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                },
                testTag = "home_play_button",
                onClick = onStartGameClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Daily Missions Card
            HomeActionCard(
                title = if (unclaimedMissionsCount > 0) "MISI HARIAN ($unclaimedMissionsCount HADIAH!)" else "MISI HARIAN TANTANGAN",
                subtitle = "Selesaikan 5 tantangan hari ini & dapatkan bonus Bintang!",
                backgroundColor = JoyOrange,
                icon = {
                    Text(text = "🎯", fontSize = 32.sp)
                },
                testTag = "home_daily_missions_button",
                onClick = onDailyMissionsClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Level Map Selection (100 Levels)
            HomeActionCard(
                title = "100 LEVEL KODING",
                subtitle = "Jelajahi 4 Dunia Lembah & Puncak Algoritma",
                backgroundColor = JoyBlue,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                },
                testTag = "home_level_map_button",
                onClick = onLevelSelectClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                // Leaderboard Button
                HomeSmallCard(
                    title = "PAPAN SKOR",
                    emoji = "🏆",
                    color = JoyPurple,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_leaderboard_button"),
                    onClick = onLeaderboardClick
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Badges Cabinet
                HomeSmallCard(
                    title = "LENCANA",
                    emoji = "🎖️",
                    color = JoyPink,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_badges_button"),
                    onClick = onBadgesClick
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Settings Button
            HomeActionCard(
                title = "PENGATURAN & PANDUAN",
                subtitle = "Suara, Musik FX & Cara Bermain Anak",
                backgroundColor = Color(0xFF7E57C2),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                },
                testTag = "home_settings_button",
                onClick = onSettingsClick
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HomeActionCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    icon: @Composable () -> Unit,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.95f),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun HomeSmallCard(
    title: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "small_card_scale"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 34.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            )
        }
    }
}
