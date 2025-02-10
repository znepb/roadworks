package me.znepb.roadworks.util

import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

object ShapeSized {
    fun createShape(x: Double, y: Double, z: Double, xs: Double, ys: Double, zs: Double)
        = VoxelShapes.cuboid(x / 16, y / 16, z / 16, (x + xs) / 16, (y + ys) / 16, (z + zs) / 16)
}