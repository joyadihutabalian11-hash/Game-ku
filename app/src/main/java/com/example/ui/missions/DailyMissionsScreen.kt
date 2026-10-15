package com.example.ui.missions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.model.DailyMission
import com.example.ui.theme.JoyGreen
import com.example.ui.theme.JoyOrange
import com.example.ui.theme.JoyPurple
import com.example.ui.theme.JoyYellow

@Composable
fun DailyMissionsScreen(
    dailyMissions: List<DailyMission>,
    onClaimReward: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val completedUnclaimedCount = dailyMissions.count { it.isCompleted && !it.isClaimed }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5))
            .padding(16.dp)
    ) {
        // Header Banner
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = JoyPurple,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🎯", fontSize = 44.sp)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "MISI HARIAN KODING",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = JoyYellow,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Selesaikan misi setiap hari untuk mendapat bonus Bintang ⭐!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (completedUnclaimedCount > 0) {
            Surface(
                color = JoyGreen,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎁 Ada $completedUnclaimedCount Hadiah Misi Siap Diklaim!",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(dailyMissions) { mission ->
                DailyMissionCard(
                    mission = mission,
                    onClaim = { onClaimReward(mission.key) }
                )
            }
        }
    }
}

@Composable
fun DailyMissionCard(
    mission: DailyMission,
    onClaim: () -> Unit
) {
    val progressFraction = (mission.currentCount.toFloat() / mission.targetCount.toFloat()).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                mission.isClaimed -> Color(0xFFE0E0E0)
                mission.isCompleted -> Color.White
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (mission.isCompleted && !mission.isClaimed) 6.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_mission_${mission.key}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = mission.iconEmoji, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = mission.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (mission.isClaimed) Color.Gray else JoyPurple
                            )
                        )
                        Text(
                            text = mission.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.DarkGray,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Reward Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = JoyOrange
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = JoyYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${mission.rewardStars}",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    color = JoyGreen,
                    trackColor = Color(0xFFEEEEEE),
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${mission.currentCount}/${mission.targetCount}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = JoyPurple
                )
            }

            // Claim Reward Button
            if (mission.isCompleted && !mission.isClaimed) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(containerColor = JoyGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("claim_mission_${mission.key}")
                ) {
                    Text(
                        text = "KLAIM HADIAH 🎁 (+${mission.rewardStars} Bintang)",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            } else if (mission.isClaimed) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selesai",
                        tint = JoyGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Hadiah Misi Telah Diklaim",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = JoyGreen
                    )
                }
            }
        }
    }
}
