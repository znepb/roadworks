package me.znepb.zrm.util

import net.minecraft.nbt.NbtIntArray
import net.minecraft.util.math.BlockPos

class MiscUtils {
    companion object {
        fun blockPosFromNbtIntArray(array: IntArray): BlockPos {
            return BlockPos(array[0], array[1], array[2])
        }
    }
}