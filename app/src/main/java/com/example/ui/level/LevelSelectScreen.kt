package com.example.ui.level

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelRepository
import com.example.model.LevelProgress
import com.example.model.WorldCategory
import com.example.ui.theme.JoyGreen
import com.example.ui.theme.JoyOrange
import com.example.ui.theme.JoyPurple
import com.example.ui.theme.JoyYellow

@Composable
fun LevelSelectScreen(
    levelProgressList: List<LevelProgress>,
    onLevelSelected: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedWorld by remember { mutableStateOf(WorldCategory.VALLEY_LOGIC) }

    val allLevels = remember { LevelRepository.getAllLevels() }
    val progressMap = remember(levelProgressList) {
        levelProgressList.associateBy { it.levelNumber }
    }

    val currentWorldLevels = remember(selectedWorld) {
        allLevels.filter { it.worldCategory == selectedWorld }
    }

    val worldsList = remember { WorldCategory.values().toList() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F0FF))
            .padding(16.dp)
    ) {
        Text(
            text = "100 LEVEL PETUALANGAN KODING",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = JoyPurple
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // World Filter Tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(worldsList) { world ->
                val isSelected = selectedWorld == world
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) JoyPurple else Color.White,
                    shadowElevation = if (isSelected) 6.dp else 2.dp,
                    modifier = Modifier
                        .clickable { selectedWorld = world }
                        .testTag("world_tab_${world.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (world) {
                                WorldCategory.VALLEY_LOGIC -> "🏔️"
                                WorldCategory.LOOP_FOREST -> "🔁"
                                WorldCategory.CONDITION_RIVER -> "⚡"
                                WorldCategory.MASTER_PEAK -> "👑"
                            },
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = world.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) JoyYellow else JoyPurple
                            )
                            Text(
                                text = world.levelRange,
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Levels Grid 1-100
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(currentWorldLevels) { level ->
                val progress = progressMap[level.id]
                // Level 1 is always unlocked by default
                val isUnlocked = level.id == 1 || (progress?.isUnlocked == true)
                val stars = progress?.stars ?: 0

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            !isUnlocked -> Color(0xFFE0E0E0)
                            stars == 3 -> JoyYellow
                            stars > 0 -> JoyGreen
                            else -> Color.White
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 4.dp else 1.dp),
                    modifier = Modifier
                        .size(80.dp)
                        .clickable(enabled = isUnlocked) { onLevelSelected(level.id) }
                        .testTag("level_card_${level.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Terkunci",
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "${level.id}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (stars == 3) JoyPurple else if (stars > 0) Color.White else JoyPurple,
                                    fontSize = 22.sp
                                )
                            )

                            // Stars Earned Row
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(3) { starIndex ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = if (starIndex < stars) JoyOrange else Color.LightGray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
