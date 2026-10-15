package com.example.engine

import com.example.model.CodingBlock
import com.example.model.ConditionType
import com.example.model.Direction
import com.example.model.GameFrameState
import com.example.model.GridPos
import com.example.model.Level

data class ExpandedInstruction(
    val blockId: String,
    val type: InstructionType,
    val count: Int = 1,
    val condition: ConditionType? = null
)

enum class InstructionType {
    MOVE, TURN_LEFT, TURN_RIGHT, JUMP, COLLECT
}

object GameEngine {

    fun simulateProgram(level: Level, program: List<CodingBlock>): List<GameFrameState> {
        val frames = mutableListOf<GameFrameState>()

        var currentPos = level.startPos
        var currentDir = level.startDirection
        val collectedGems = mutableSetOf<GridPos>()
        val activatedSwitches = mutableSetOf<GridPos>()

        // Initial Frame
        frames.add(
            GameFrameState(
                stepIndex = 0,
                activeBlockId = null,
                playerPos = currentPos,
                playerDirection = currentDir,
                collectedGems = collectedGems.toSet(),
                activatedSwitches = activatedSwitches.toSet(),
                message = "Mulai koding!"
            )
        )

        var stepCount = 0
        val expandedList = expandProgram(program)

        for (instruction in expandedList) {
            stepCount++
            val blockId = instruction.blockId

            when (instruction.type) {
                InstructionType.MOVE -> {
                    val nextPos = getNextPos(currentPos, currentDir)
                    if (!isWithinGrid(nextPos, level.gridWidth, level.gridHeight)) {
                        frames.add(
                            GameFrameState(
                                stepIndex = stepCount,
                                activeBlockId = blockId,
                                playerPos = currentPos,
                                playerDirection = currentDir,
                                collectedGems = collectedGems.toSet(),
                                activatedSwitches = activatedSwitches.toSet(),
                                isFailed = true,
                                failReason = "Joy keluar dari area permainan! ⛔"
                            )
                        )
                        return frames
                    }

                    if (level.obstacles.contains(nextPos)) {
                        frames.add(
                            GameFrameState(
                                stepIndex = stepCount,
                                activeBlockId = blockId,
                                playerPos = nextPos,
                                playerDirection = currentDir,
                                collectedGems = collectedGems.toSet(),
                                activatedSwitches = activatedSwitches.toSet(),
                                isFailed = true,
                                failReason = "Joy menabrak batu rintangan! 💥"
                            )
                        )
                        return frames
                    }

                    if (level.waterTiles.contains(nextPos)) {
                        frames.add(
                            GameFrameState(
                                stepIndex = stepCount,
                                activeBlockId = blockId,
                                playerPos = nextPos,
                                playerDirection = currentDir,
                                collectedGems = collectedGems.toSet(),
                                activatedSwitches = activatedSwitches.toSet(),
                                isFailed = true,
                                failReason = "Joy jatuh ke dalam air! Gunakan 'Melompat' 🦘"
                            )
                        )
                        return frames
                    }

                    currentPos = nextPos
                    frames.add(
                        GameFrameState(
                            stepIndex = stepCount,
                            activeBlockId = blockId,
                            playerPos = currentPos,
                            playerDirection = currentDir,
                            collectedGems = collectedGems.toSet(),
                            activatedSwitches = activatedSwitches.toSet(),
                            message = "Joy maju 1 langkah"
                        )
                    )
                }

                InstructionType.TURN_LEFT -> {
                    currentDir = currentDir.turnLeft()
                    frames.add(
                        GameFrameState(
                            stepIndex = stepCount,
                            activeBlockId = blockId,
                            playerPos = currentPos,
                            playerDirection = currentDir,
                            collectedGems = collectedGems.toSet(),
                            activatedSwitches = activatedSwitches.toSet(),
                            message = "Joy belok kiri"
                        )
                    )
                }

                InstructionType.TURN_RIGHT -> {
                    currentDir = currentDir.turnRight()
                    frames.add(
                        GameFrameState(
                            stepIndex = stepCount,
                            activeBlockId = blockId,
                            playerPos = currentPos,
                            playerDirection = currentDir,
                            collectedGems = collectedGems.toSet(),
                            activatedSwitches = activatedSwitches.toSet(),
                            message = "Joy belok kanan"
                        )
                    )
                }

                InstructionType.JUMP -> {
                    val next1 = getNextPos(currentPos, currentDir)
                    val next2 = getNextPos(next1, currentDir)

                    if (!isWithinGrid(next2, level.gridWidth, level.gridHeight)) {
                        frames.add(
                            GameFrameState(
                                stepIndex = stepCount,
                                activeBlockId = blockId,
                                playerPos = currentPos,
                                playerDirection = currentDir,
                                collectedGems = collectedGems.toSet(),
                                activatedSwitches = activatedSwitches.toSet(),
                                isFailed = true,
                                failReason = "Lompatan keluar batas! ⛔"
                            )
                        )
                        return frames
                    }

                    if (level.obstacles.contains(next2)) {
                        frames.add(
                            GameFrameState(
                                stepIndex = stepCount,
                                activeBlockId = blockId,
                                playerPos = next2,
                                playerDirection = currentDir,
                                collectedGems = collectedGems.toSet(),
                                activatedSwitches = activatedSwitches.toSet(),
                                isFailed = true,
                                failReason = "Joy melompat dan menabrak batu! 💥"
                            )
                        )
                        return frames
                    }

                    currentPos = next2
                    frames.add(
                        GameFrameState(
                            stepIndex = stepCount,
                            activeBlockId = blockId,
                            playerPos = currentPos,
                            playerDirection = currentDir,
                            collectedGems = collectedGems.toSet(),
                            activatedSwitches = activatedSwitches.toSet(),
                            message = "Joy melompat tinggi! 🦘"
                        )
                    )
                }

                InstructionType.COLLECT -> {
                    if (level.gems.contains(currentPos)) {
                        collectedGems.add(currentPos)
                        frames.add(
                            GameFrameState(
                                stepIndex = stepCount,
                                activeBlockId = blockId,
                                playerPos = currentPos,
                                playerDirection = currentDir,
                                collectedGems = collectedGems.toSet(),
                                activatedSwitches = activatedSwitches.toSet(),
                                message = "Berhasil mengambil permata! 💎"
                            )
                        )
                    } else {
                        frames.add(
                            GameFrameState(
                                stepIndex = stepCount,
                                activeBlockId = blockId,
                                playerPos = currentPos,
                                playerDirection = currentDir,
                                collectedGems = collectedGems.toSet(),
                                activatedSwitches = activatedSwitches.toSet(),
                                message = "Tidak ada permata di tempat ini"
                            )
                        )
                    }
                }
            }

            // Check Win condition at current position
            if (currentPos == level.goalPos) {
                val allGemsCollected = collectedGems.containsAll(level.gems)
                if (allGemsCollected) {
                    frames.add(
                        GameFrameState(
                            stepIndex = stepCount + 1,
                            activeBlockId = null,
                            playerPos = currentPos,
                            playerDirection = currentDir,
                            collectedGems = collectedGems.toSet(),
                            activatedSwitches = activatedSwitches.toSet(),
                            isWin = true,
                            message = "Hore! Joy sampai di garis Finis! 🎉"
                        )
                    )
                    return frames
                }
            }
        }

        // Program finished but goal not reached
        if (currentPos != level.goalPos) {
            frames.add(
                GameFrameState(
                    stepIndex = stepCount + 1,
                    activeBlockId = null,
                    playerPos = currentPos,
                    playerDirection = currentDir,
                    collectedGems = collectedGems.toSet(),
                    activatedSwitches = activatedSwitches.toSet(),
                    isFailed = true,
                    failReason = "Joy belum sampai di Finis! Tambahkan blok koding lagi. 🤔"
                )
            )
        } else if (!collectedGems.containsAll(level.gems)) {
            frames.add(
                GameFrameState(
                    stepIndex = stepCount + 1,
                    activeBlockId = null,
                    playerPos = currentPos,
                    playerDirection = currentDir,
                    collectedGems = collectedGems.toSet(),
                    activatedSwitches = activatedSwitches.toSet(),
                    isFailed = true,
                    failReason = "Ada permata yang belum diambil! Ambil semua permata. 💎"
                )
            )
        }

        return frames
    }

    private fun expandProgram(program: List<CodingBlock>): List<ExpandedInstruction> {
        val result = mutableListOf<ExpandedInstruction>()
        for (block in program) {
            when (block) {
                is CodingBlock.MoveForward -> result.add(ExpandedInstruction(block.id, InstructionType.MOVE))
                is CodingBlock.TurnLeft -> result.add(ExpandedInstruction(block.id, InstructionType.TURN_LEFT))
                is CodingBlock.TurnRight -> result.add(ExpandedInstruction(block.id, InstructionType.TURN_RIGHT))
                is CodingBlock.Jump -> result.add(ExpandedInstruction(block.id, InstructionType.JUMP))
                is CodingBlock.Collect -> result.add(ExpandedInstruction(block.id, InstructionType.COLLECT))
                is CodingBlock.Repeat -> {
                    for (i in 0 until block.count) {
                        result.addAll(expandProgram(block.innerBlocks))
                    }
                }
                is CodingBlock.IfCondition -> {
                    result.addAll(expandProgram(block.innerBlocks))
                }
            }
        }
        return result
    }

    private fun getNextPos(pos: GridPos, dir: Direction): GridPos = when (dir) {
        Direction.UP -> GridPos(pos.x, pos.y - 1)
        Direction.RIGHT -> GridPos(pos.x + 1, pos.y)
        Direction.DOWN -> GridPos(pos.x, pos.y + 1)
        Direction.LEFT -> GridPos(pos.x - 1, pos.y)
    }

    private fun isWithinGrid(pos: GridPos, width: Int, height: Int): Boolean {
        return pos.x in 0 until width && pos.y in 0 until height
    }

    fun countTotalBlocks(program: List<CodingBlock>): Int {
        var count = 0
        for (block in program) {
            count++
            if (block is CodingBlock.Repeat) {
                count += countTotalBlocks(block.innerBlocks)
            } else if (block is CodingBlock.IfCondition) {
                count += countTotalBlocks(block.innerBlocks)
            }
        }
        return count
    }

    fun calculateStars(level: Level, program: List<CodingBlock>): Int {
        val blocksUsed = countTotalBlocks(program)
        return when {
            blocksUsed <= level.maxBlocksFor3Stars -> 3
            blocksUsed <= level.maxBlocksFor3Stars + 3 -> 2
            else -> 1
        }
    }
}
