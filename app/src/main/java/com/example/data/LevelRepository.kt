package com.example.data

import com.example.model.CodingBlock
import com.example.model.ConditionType
import com.example.model.Direction
import com.example.model.GridPos
import com.example.model.Level
import com.example.model.WorldCategory

object LevelRepository {

    private val levelCache = mutableMapOf<Int, Level>()

    fun getLevel(levelId: Int): Level {
        return levelCache.getOrPut(levelId) {
            generateLevel(levelId)
        }
    }

    fun getAllLevels(): List<Level> {
        return (1..100).map { getLevel(it) }
    }

    private fun generateLevel(id: Int): Level {
        return when (id) {
            1 -> Level(
                id = 1,
                title = "Langkah Perdana",
                worldCategory = WorldCategory.VALLEY_LOGIC,
                gridWidth = 5, gridHeight = 5,
                startPos = GridPos(1, 2), startDirection = Direction.RIGHT,
                goalPos = GridPos(3, 2),
                availableBlocks = listOf(CodingBlock.MoveForward()),
                maxBlocksFor3Stars = 2,
                hintText = "Tambahkan 2 blok 'Maju 1 Langkah' untuk mencapai Bendera Finis!"
            )
            2 -> Level(
                id = 2,
                title = "Jalan Lurus Joy",
                worldCategory = WorldCategory.VALLEY_LOGIC,
                gridWidth = 5, gridHeight = 5,
                startPos = GridPos(0, 2), startDirection = Direction.RIGHT,
                goalPos = GridPos(3, 2),
                availableBlocks = listOf(CodingBlock.MoveForward()),
                maxBlocksFor3Stars = 3,
                hintText = "Joy butuh 3 langkah maju ke depan!"
            )
            3 -> Level(
                id = 3,
                title = "Belok Ke Kanan",
                worldCategory = WorldCategory.VALLEY_LOGIC,
                gridWidth = 5, gridHeight = 5,
                startPos = GridPos(1, 1), startDirection = Direction.RIGHT,
                goalPos = GridPos(2, 3),
                obstacles = setOf(GridPos(2, 1)),
                availableBlocks = listOf(CodingBlock.MoveForward(), CodingBlock.TurnRight()),
                maxBlocksFor3Stars = 4,
                hintText = "Gunakan blok 'Belok Kanan' agar Joy tidak menabrak batu!"
            )
            4 -> Level(
                id = 4,
                title = "Belok Ke Kiri",
                worldCategory = WorldCategory.VALLEY_LOGIC,
                gridWidth = 5, gridHeight = 5,
                startPos = GridPos(1, 3), startDirection = Direction.RIGHT,
                goalPos = GridPos(2, 1),
                obstacles = setOf(GridPos(2, 3)),
                availableBlocks = listOf(CodingBlock.MoveForward(), CodingBlock.TurnLeft()),
                maxBlocksFor3Stars = 4,
                hintText = "Gunakan 'Belok Kiri' lalu maju ke atas!"
            )
            5 -> Level(
                id = 5,
                title = "Permata Pertama",
                worldCategory = WorldCategory.VALLEY_LOGIC,
                gridWidth = 5, gridHeight = 5,
                startPos = GridPos(1, 2), startDirection = Direction.RIGHT,
                goalPos = GridPos(3, 2),
                gems = setOf(GridPos(2, 2)),
                availableBlocks = listOf(CodingBlock.MoveForward(), CodingBlock.Collect()),
                maxBlocksFor3Stars = 4,
                hintText = "Maju, lalu gunakan blok 'Ambil Permata' saat berdiri di atas permata!"
            )
            in 6..25 -> generateValleyLevel(id)
            in 26..50 -> generateLoopLevel(id)
            in 51..75 -> generateConditionLevel(id)
            else -> generateMasterLevel(id)
        }
    }

    private fun generateValleyLevel(id: Int): Level {
        val width = 5 + (id % 3)
        val height = 5 + (id % 3)
        val startX = 1
        val startY = 1 + (id % 3)
        val goalX = startX + 2 + (id % 3)
        val goalY = startY + ((id * 2) % 3)

        val obstacles = mutableSetOf<GridPos>()
        if (id % 2 == 0) obstacles.add(GridPos(startX + 1, startY))
        if (id % 3 == 0) obstacles.add(GridPos(goalX, startY))

        val gems = mutableSetOf<GridPos>()
        gems.add(GridPos(startX + 1, startY + 1))
        if (id > 15) gems.add(GridPos(startX + 2, startY))

        return Level(
            id = id,
            title = "Lembah Tantangan $id",
            worldCategory = WorldCategory.VALLEY_LOGIC,
            gridWidth = width, gridHeight = height,
            startPos = GridPos(startX, startY), startDirection = Direction.RIGHT,
            goalPos = GridPos(goalX, goalY),
            obstacles = obstacles,
            gems = gems,
            availableBlocks = listOf(
                CodingBlock.MoveForward(),
                CodingBlock.TurnLeft(),
                CodingBlock.TurnRight(),
                CodingBlock.Collect()
            ),
            maxBlocksFor3Stars = 5 + (id % 4),
            hintText = "Hindari batu rintangan dan kumpulkan permata sebelum ke finis!"
        )
    }

    private fun generateLoopLevel(id: Int): Level {
        val stepCount = 3 + ((id - 25) % 4)
        val width = 6 + (id % 2)
        val height = 6 + (id % 2)

        return Level(
            id = id,
            title = "Hutan Perulangan $id",
            worldCategory = WorldCategory.LOOP_FOREST,
            gridWidth = width, gridHeight = height,
            startPos = GridPos(1, 1), startDirection = Direction.RIGHT,
            goalPos = GridPos(1 + (id % 4), 1 + (id % 4)),
            gems = setOf(GridPos(2, 1), GridPos(3, 1), GridPos(4, 1)),
            availableBlocks = listOf(
                CodingBlock.MoveForward(),
                CodingBlock.TurnRight(),
                CodingBlock.TurnLeft(),
                CodingBlock.Collect(),
                CodingBlock.Repeat(count = stepCount)
            ),
            maxBlocksFor3Stars = 4 + (id % 3),
            hintText = "Gunakan blok 'Ulangi' agar kode kodingmu jauh lebih ringkas dan dapat 3 bintang!"
        )
    }

    private fun generateConditionLevel(id: Int): Level {
        val width = 6 + (id % 3)
        val height = 6 + (id % 3)

        val waterTiles = setOf(GridPos(2, 2), GridPos(3, 2))
        val gems = setOf(GridPos(2, 1), GridPos(4, 3))

        return Level(
            id = id,
            title = "Sungai Kondisi $id",
            worldCategory = WorldCategory.CONDITION_RIVER,
            gridWidth = width, gridHeight = height,
            startPos = GridPos(1, 2), startDirection = Direction.RIGHT,
            goalPos = GridPos(5, 2),
            waterTiles = waterTiles,
            gems = gems,
            availableBlocks = listOf(
                CodingBlock.MoveForward(),
                CodingBlock.TurnLeft(),
                CodingBlock.TurnRight(),
                CodingBlock.Jump(),
                CodingBlock.Collect(),
                CodingBlock.Repeat(count = 2),
                CodingBlock.IfCondition(condition = ConditionType.PATH_AHEAD)
            ),
            maxBlocksFor3Stars = 6 + (id % 3),
            hintText = "Gunakan blok 'Melompat' untuk melompati sungai air biru dan kumpulkan permata!"
        )
    }

    private fun generateMasterLevel(id: Int): Level {
        val width = 7 + (id % 2)
        val height = 7 + (id % 2)

        val obstacles = setOf(GridPos(2, 2), GridPos(3, 3), GridPos(4, 2), GridPos(5, 4))
        val waterTiles = setOf(GridPos(3, 2), GridPos(4, 3))
        val gems = setOf(GridPos(2, 1), GridPos(4, 1), GridPos(5, 3))

        return Level(
            id = id,
            title = "Puncak Algoritma $id",
            worldCategory = WorldCategory.MASTER_PEAK,
            gridWidth = width, gridHeight = height,
            startPos = GridPos(1, 1), startDirection = Direction.RIGHT,
            goalPos = GridPos(6, 6),
            obstacles = obstacles,
            waterTiles = waterTiles,
            gems = gems,
            availableBlocks = listOf(
                CodingBlock.MoveForward(),
                CodingBlock.TurnLeft(),
                CodingBlock.TurnRight(),
                CodingBlock.Jump(),
                CodingBlock.Collect(),
                CodingBlock.Repeat(count = 3),
                CodingBlock.IfCondition(condition = ConditionType.GEM_PRESENT)
            ),
            maxBlocksFor3Stars = 7 + (id % 3),
            hintText = "Rencanakan strategi kodingmu secara teliti! Gabungkan Loop dan Jump untuk skor tertinggi."
        )
    }
}
