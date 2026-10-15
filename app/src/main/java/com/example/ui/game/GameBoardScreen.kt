package com.example.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CodingBlock
import com.example.model.GameFrameState
import com.example.model.GridPos
import com.example.model.Level
import com.example.ui.theme.BlockCollectColor
import com.example.ui.theme.BlockIfColor
import com.example.ui.theme.BlockJumpColor
import com.example.ui.theme.BlockMoveColor
import com.example.ui.theme.BlockRepeatColor
import com.example.ui.theme.BlockTurnColor
import com.example.ui.theme.JoyGreen
import com.example.ui.theme.JoyOrange
import com.example.ui.theme.JoyPurple
import com.example.ui.theme.JoyRed
import com.example.ui.theme.JoyYellow
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GameBoardScreen(
    level: Level,
    programBlocks: List<CodingBlock>,
    simulationFrames: List<GameFrameState>,
    currentFrameIndex: Int,
    isRunningCode: Boolean,
    onAddBlock: (CodingBlock) -> Unit,
    onRemoveBlock: (Int) -> Unit,
    onUpdateRepeatCount: (Int, Int) -> Unit,
    onClearProgram: () -> Unit,
    onRunCode: () -> Unit,
    onStepForward: () -> Unit,
    onStopSimulation: () -> Unit,
    onSetSpeed: (Long) -> Unit
) {
    var showHint by remember { mutableStateOf(false) }

    val activeFrame = if (simulationFrames.isNotEmpty() && currentFrameIndex in simulationFrames.indices) {
        simulationFrames[currentFrameIndex]
    } else {
        GameFrameState(
            stepIndex = 0,
            activeBlockId = null,
            playerPos = level.startPos,
            playerDirection = level.startDirection,
            collectedGems = emptySet(),
            activatedSwitches = emptySet(),
            message = "Susun blok koding di bawah!"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEDE7F6),
                        Color(0xFFD1C4E9),
                        Color(0xFFEDE7F6)
                    )
                )
            )
            .padding(8.dp)
    ) {
        // Top Level Header Bar
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = JoyPurple,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "LEVEL ${level.id}: ${level.title}",
                        fontWeight = FontWeight.Black,
                        color = JoyYellow,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Target Bintang 3: <= ${level.maxBlocksFor3Stars} Blok Koding",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Button(
                    onClick = { showHint = !showHint },
                    colors = ButtonDefaults.buttonColors(containerColor = JoyOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("hint_toggle_button")
                ) {
                    Text(
                        text = if (showHint) "Tutup Tips" else "💡 Tips",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // Animated Hint Banner
        AnimatedVisibility(visible = showHint) {
            Surface(
                color = JoyYellow.copy(alpha = 0.95f),
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "💡 ${level.hintText}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    ),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Smooth Canvas Grid Game Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFAED581), Color(0xFF689F38)),
                        radius = 800f
                    )
                )
                .border(4.dp, Color(0xFF33691E), RoundedCornerShape(22.dp))
                .testTag("game_grid_canvas"),
            contentAlignment = Alignment.Center
        ) {
            CanvasGridView(
                level = level,
                frameState = activeFrame
            )
        }

        // Animated Status Message Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
                activeFrame.isWin -> JoyGreen
                activeFrame.isFailed -> JoyRed
                else -> JoyPurple
            },
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = activeFrame.failReason.ifEmpty { activeFrame.message.ifEmpty { "Siap koding!" } },
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }

        // Workspace Program Area
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
                .padding(vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ALGORITMA KODINGKU (${programBlocks.size} Blok)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = JoyPurple
                    )

                    if (programBlocks.isNotEmpty()) {
                        Text(
                            text = "Hapus Semua 🗑️",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = JoyRed,
                            modifier = Modifier
                                .clickable { onClearProgram() }
                                .testTag("clear_program_button")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (programBlocks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, Color.LightGray, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sentuh blok di bawah untuk menyusun urutan koding 🧩",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        programBlocks.forEachIndexed { index, block ->
                            val isActive = activeFrame.activeBlockId == block.id
                            CodingBlockItem(
                                block = block,
                                index = index,
                                isActive = isActive,
                                onRemove = { onRemoveBlock(index) },
                                onUpdateRepeatCount = { count -> onUpdateRepeatCount(index, count) }
                            )
                        }
                    }
                }
            }
        }

        // Block Palette Area
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFEDE7F6),
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Column(modifier = Modifier.padding(6.dp)) {
                Text(
                    text = "BLOK TERSEDIA (SENTUH UNTUK MENAMBAH):",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = JoyPurple
                )

                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(level.availableBlocks) { _, block ->
                        PaletteBlockButton(
                            block = block,
                            onAdd = { onAddBlock(block) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Floating Control Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // PLAY / STOP BUTTON
            Button(
                onClick = onRunCode,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunningCode) JoyRed else JoyGreen
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .weight(1.8f)
                    .height(52.dp)
                    .testTag("run_code_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isRunningCode) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = "Run",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRunningCode) "BERHENTI" else "JALANKAN KODING",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            // STEP FORWARD BUTTON
            Button(
                onClick = onStepForward,
                colors = ButtonDefaults.buttonColors(containerColor = JoyOrange),
                shape = RoundedCornerShape(18.dp),
                enabled = !isRunningCode,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("step_forward_button")
            ) {
                Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Langkah", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "LANGKAH", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // RESET BUTTON
            IconButton(
                onClick = onStopSimulation,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(JoyPurple)
                    .testTag("reset_simulation_button")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
            }
        }
    }
}

@Composable
fun CanvasGridView(
    level: Level,
    frameState: GameFrameState
) {
    // Smooth Angle Rotation Interpolation
    val rotationAngle by animateFloatAsState(
        targetValue = frameState.playerDirection.angle,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "player_rotation"
    )

    // Pulsing Glow Transition for Goal & Gems
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            for (y in 0 until level.gridHeight) {
                Row {
                    for (x in 0 until level.gridWidth) {
                        val pos = GridPos(x, y)
                        val isPlayer = pos == frameState.playerPos
                        val isGoal = pos == level.goalPos
                        val isObstacle = level.obstacles.contains(pos)
                        val isWater = level.waterTiles.contains(pos)
                        val isGem = level.gems.contains(pos) && !frameState.collectedGems.contains(pos)

                        // Smooth Position Animation Offsets
                        val animScale by animateFloatAsState(
                            targetValue = if (isPlayer) 1.1f else 1.0f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "tile_scale"
                        )

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .padding(2.dp)
                                .scale(animScale)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isWater -> Color(0xFF29B6F6)
                                        isGoal -> JoyYellow
                                        (x + y) % 2 == 0 -> Color(0xFFAED581)
                                        else -> Color(0xFF9CCC65)
                                    }
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (isPlayer) JoyYellow else Color(0xFF33691E),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isPlayer -> {
                                    Text(
                                        text = "🤖",
                                        fontSize = 26.sp,
                                        modifier = Modifier.rotate(rotationAngle)
                                    )
                                }
                                isGoal -> {
                                    Box(modifier = Modifier.scale(pulseScale)) {
                                        Text(text = "🚩", fontSize = 24.sp)
                                    }
                                }
                                isObstacle -> Text(text = "🪨", fontSize = 20.sp)
                                isWater -> Text(text = "🌊", fontSize = 20.sp)
                                isGem -> {
                                    Box(modifier = Modifier.scale(pulseScale)) {
                                        Text(text = "💎", fontSize = 20.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodingBlockItem(
    block: CodingBlock,
    index: Int,
    isActive: Boolean,
    onRemove: () -> Unit,
    onUpdateRepeatCount: (Int) -> Unit
) {
    val blockColor = getBlockColor(block)

    // Pulse animation for active executing block
    val infiniteTransition = rememberInfiniteTransition(label = "block_active_glow")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = blockColor,
        shadowElevation = if (isActive) 10.dp else 3.dp,
        modifier = Modifier
            .border(
                width = if (isActive) 3.5.dp else 0.dp,
                color = if (isActive) JoyYellow.copy(alpha = borderAlpha) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("workspace_block_$index")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getBlockIcon(block),
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column {
                Text(
                    text = getBlockLabel(block),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = Color.White
                )

                if (block is CodingBlock.Repeat) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onUpdateRepeatCount(block.count - 1) },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "-", tint = Color.White)
                        }

                        Text(
                            text = "${block.count}x",
                            fontWeight = FontWeight.Black,
                            color = JoyYellow,
                            fontSize = 13.sp
                        )

                        IconButton(
                            onClick = { onUpdateRepeatCount(block.count + 1) },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "+", tint = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Hapus",
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun PaletteBlockButton(
    block: CodingBlock,
    onAdd: () -> Unit
) {
    val blockColor = getBlockColor(block)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = blockColor,
        shadowElevation = 3.dp,
        modifier = Modifier
            .clickable { onAdd() }
            .testTag("palette_block_${getBlockLabel(block)}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = getBlockIcon(block), fontSize = 18.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = getBlockLabel(block),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

fun getBlockColor(block: CodingBlock): Color = when (block) {
    is CodingBlock.MoveForward -> BlockMoveColor
    is CodingBlock.TurnLeft -> BlockTurnColor
    is CodingBlock.TurnRight -> BlockTurnColor
    is CodingBlock.Jump -> BlockJumpColor
    is CodingBlock.Collect -> BlockCollectColor
    is CodingBlock.Repeat -> BlockRepeatColor
    is CodingBlock.IfCondition -> BlockIfColor
}

fun getBlockIcon(block: CodingBlock): String = when (block) {
    is CodingBlock.MoveForward -> "⬆️"
    is CodingBlock.TurnLeft -> "↺"
    is CodingBlock.TurnRight -> "↻"
    is CodingBlock.Jump -> "🦘"
    is CodingBlock.Collect -> "💎"
    is CodingBlock.Repeat -> "🔁"
    is CodingBlock.IfCondition -> "❓"
}

fun getBlockLabel(block: CodingBlock): String = when (block) {
    is CodingBlock.MoveForward -> "Maju 1"
    is CodingBlock.TurnLeft -> "Belok Kiri"
    is CodingBlock.TurnRight -> "Belok Kanan"
    is CodingBlock.Jump -> "Melompat"
    is CodingBlock.Collect -> "Ambil Gem"
    is CodingBlock.Repeat -> "Ulangi"
    is CodingBlock.IfCondition -> "Jika Ada"
}
