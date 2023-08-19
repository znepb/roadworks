package me.znepb.roadworks.util

import net.minecraft.util.math.BlockPos

class MiscUtils {
    companion object {
        fun blockPosFromNbtIntArray(array: IntArray): BlockPos {
            return BlockPos(array[0], array[1], array[2])
        }

        fun blockPosToNbtIntArray(blockPos: BlockPos): IntArray {
            return intArrayOf(blockPos.x, blockPos.y, blockPos.z)
        }
    }
}