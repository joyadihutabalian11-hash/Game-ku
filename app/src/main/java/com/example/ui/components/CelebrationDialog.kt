package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.JoyGreen
import com.example.ui.theme.JoyPurple
import com.example.ui.theme.JoyYellow

@Composable
fun CelebrationDialog(
    levelId: Int,
    starsEarned: Int,
    blocksUsed: Int,
    targetBlocks: Int,
    unlockedBadgeTitle: String? = null,
    onNextLevelClick: () -> Unit,
    onReplayClick: () -> Unit,
    onLevelSelectClick: () -> Unit
) {
    val star1Scale = remember { Animatable(0f) }
    val star2Scale = remember { Animatable(0f) }
    val star3Scale = remember { Animatable(0f) }

    LaunchedEffect(starsEarned) {
        if (starsEarned >= 1) {
            star1Scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        if (starsEarned >= 2) {
            star2Scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        if (starsEarned >= 3) {
            star3Scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    val praiseText = when (starsEarned) {
        3 -> "LUAR BIASA! 🌟 Bintang Sempurna!"
        2 -> "HEBAT BANGET! 🎉 Nyaris Sempurna!"
        else -> "BAGUS! 👍 Teruskan Semangatmu!"
    }

    Dialog(onDismissRequest = { /* Modal stays open */ }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("celebration_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Robot Mascot Emoji
                Text(
                    text = "🤖🎉",
                    fontSize = 48.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "LEVEL $levelId SELESAI!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = JoyPurple
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bouncing Stars Row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.scale(star1Scale.value)) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Bintang 1",
                            tint = if (starsEarned >= 1) JoyYellow else Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.scale(star2Scale.value)) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Bintang 2",
                            tint = if (starsEarned >= 2) JoyYellow else Color.LightGray,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.scale(star3Scale.value)) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Bintang 3",
                            tint = if (starsEarned >= 3) JoyYellow else Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = praiseText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = JoyPurple,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Koding Terpakai: $blocksUsed Blok (Target Bintang 3: <= $targetBlocks)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                )

                // Unlocked Badge Card
                if (unlockedBadgeTitle != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = JoyYellow.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🏆", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Lencana Baru Ditemukan!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = unlockedBadgeTitle,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = JoyPurple
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Button(
                    onClick = onNextLevelClick,
                    colors = ButtonDefaults.buttonColors(containerColor = JoyGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("celebration_next_level_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LANJUT LEVEL",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Lanjut",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onReplayClick,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("celebration_replay_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Ulangi"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Ulangi")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onLevelSelectClick,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("celebration_level_select_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Menu Level"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Pilih Level")
                    }
                }
            }
        }
    }
}
