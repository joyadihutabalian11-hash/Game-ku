package com.example.model

import java.util.UUID

enum class ConditionType(val label: String) {
    PATH_AHEAD("Ada Jalan di Depan"),
    GEM_PRESENT("Ada Permata di Sini")
}

sealed class CodingBlock(
    val id: String = UUID.randomUUID().toString()
) {
    abstract fun copyBlock(): CodingBlock

    data class MoveForward(val instanceId: String = UUID.randomUUID().toString()) : CodingBlock(instanceId) {
        override fun copyBlock(): CodingBlock = MoveForward()
    }

    data class TurnLeft(val instanceId: String = UUID.randomUUID().toString()) : CodingBlock(instanceId) {
        override fun copyBlock(): CodingBlock = TurnLeft()
    }

    data class TurnRight(val instanceId: String = UUID.randomUUID().toString()) : CodingBlock(instanceId) {
        override fun copyBlock(): CodingBlock = TurnRight()
    }

    data class Jump(val instanceId: String = UUID.randomUUID().toString()) : CodingBlock(instanceId) {
        override fun copyBlock(): CodingBlock = Jump()
    }

    data class Collect(val instanceId: String = UUID.randomUUID().toString()) : CodingBlock(instanceId) {
        override fun copyBlock(): CodingBlock = Collect()
    }

    data class Repeat(
        var count: Int = 2,
        val innerBlocks: MutableList<CodingBlock> = mutableListOf(MoveForward()),
        val instanceId: String = UUID.randomUUID().toString()
    ) : CodingBlock(instanceId) {
        override fun copyBlock(): CodingBlock = Repeat(
            count = count,
            innerBlocks = innerBlocks.map { it.copyBlock() }.toMutableList()
        )
    }

    data class IfCondition(
        var condition: ConditionType = ConditionType.PATH_AHEAD,
        val innerBlocks: MutableList<CodingBlock> = mutableListOf(MoveForward()),
        val instanceId: String = UUID.randomUUID().toString()
    ) : CodingBlock(instanceId) {
        override fun copyBlock(): CodingBlock = IfCondition(
            condition = condition,
            innerBlocks = innerBlocks.map { it.copyBlock() }.toMutableList()
        )
    }
}

val ALL_BLOCK_TYPES: List<CodingBlock> = listOf(
    CodingBlock.MoveForward(),
    CodingBlock.TurnLeft(),
    CodingBlock.TurnRight(),
    CodingBlock.Jump(),
    CodingBlock.Collect(),
    CodingBlock.Repeat(count = 2),
    CodingBlock.IfCondition(condition = ConditionType.PATH_AHEAD)
)
