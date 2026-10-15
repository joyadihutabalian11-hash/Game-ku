package com.example.model

enum class Direction(val angle: Float) {
    UP(0f),
    RIGHT(90f),
    DOWN(180f),
    LEFT(270f);

    fun turnLeft(): Direction = when (this) {
        UP -> LEFT
        LEFT -> DOWN
        DOWN -> RIGHT
        RIGHT -> UP
    }

    fun turnRight(): Direction = when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
    }
}

data class GridPos(val x: Int, val y: Int)

enum class WorldCategory(val id: Int, val title: String, val levelRange: String, val description: String) {
    VALLEY_LOGIC(1, "Lembah Logika", "Level 1 - 25", "Dasar Pergerakan & Arah Navigasi"),
    LOOP_FOREST(2, "Hutan Repetisi", "Level 26 - 50", "Perulangan & Efisiensi Koding (Loop)"),
    CONDITION_RIVER(3, "Sungai Kondisi", "Level 51 - 75", "Lompatan & Percabangan Logika (If)"),
    MASTER_PEAK(4, "Puncak Pemecah Masalah", "Level 76 - 100", "Tantangan Kompleks & Algoritma Tingkat Tinggi")
}

data class Level(
    val id: Int,
    val title: String,
    val worldCategory: WorldCategory,
    val gridWidth: Int = 6,
    val gridHeight: Int = 6,
    val startPos: GridPos,
    val startDirection: Direction = Direction.RIGHT,
    val goalPos: GridPos,
    val obstacles: Set<GridPos> = emptySet(), // Wall/Rocks
    val waterTiles: Set<GridPos> = emptySet(), // Water hazards require Jump
    val gems: Set<GridPos> = emptySet(), // Gems to collect
    val switches: Set<GridPos> = emptySet(), // Switches to press
    val availableBlocks: List<CodingBlock> = emptyList(),
    val maxBlocksFor3Stars: Int = 5,
    val hintText: String = "Susun blok untuk membawa Joy ke Bendera Finis!"
)

data class GameFrameState(
    val stepIndex: Int,
    val activeBlockId: String?,
    val playerPos: GridPos,
    val playerDirection: Direction,
    val collectedGems: Set<GridPos>,
    val activatedSwitches: Set<GridPos>,
    val message: String = "",
    val isWin: Boolean = false,
    val isFailed: Boolean = false,
    val failReason: String = ""
)

data class LevelResult(
    val levelId: Int,
    val stars: Int, // 1..3
    val blocksUsed: Int,
    val totalTimeSeconds: Int,
    val isFirstTimeCompletion: Boolean
)
