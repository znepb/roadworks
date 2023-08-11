package me.znepb.zrm.util

import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes


class RotateVoxelShape {
    companion object {
        // https://forums.minecraftforge.net/topic/74979-1144-rotate-voxel-shapes/

        // no idea what to name this func...
        private fun get2dHoriz(dir: Direction): Int {
            return when(dir) {
                Direction.UP -> 2
                Direction.NORTH -> 1
                Direction.DOWN -> 0
                Direction.SOUTH -> 3
                else -> 0
            }
        }

        private fun rotateVoxelShapeVertically(from: Direction, to: Direction, shape: VoxelShape): VoxelShape {
            val buffer = arrayOf<VoxelShape>(shape, VoxelShapes.empty())
            val times: Int = (get2dHoriz(to) - get2dHoriz(from) + 4) % 4

            for (i in 0 until times) {
                buffer[0].forEachBox { minX, minY, minZ, maxX, maxY, maxZ ->
                    buffer[1] = VoxelShapes.union(
                        buffer[1],
                        VoxelShapes.cuboid(minX, 1 - maxZ, minY, maxX, 1 - minZ, maxY)
                    )
                }
                buffer[0] = buffer[1]
                buffer[1] = VoxelShapes.empty()
            }

            return buffer[0]
        }

        private fun rotateVoxelShapeHorizontally(from: Direction, to: Direction, shape: VoxelShape): VoxelShape {
            val buffer = arrayOf<VoxelShape>(shape, VoxelShapes.empty())
            val times: Int = (to.horizontal - from.horizontal + 4) % 4

            for (i in 0 until times) {
                buffer[0].forEachBox { minX, minY, minZ, maxX, maxY, maxZ ->
                    buffer[1] = VoxelShapes.union(
                        buffer[1],
                        VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
                    )
                }
                buffer[0] = buffer[1]
                buffer[1] = VoxelShapes.empty()
            }

            return buffer[0]
        }

        fun rotateVoxelShape(shape: VoxelShape, from: Direction, to: Direction): VoxelShape {
            if(from.horizontal == -1 && to.horizontal == -1) {
                // Flip
                return rotateVoxelShapeVertically(from, to, shape)
            } else if(from.horizontal == -1) {
                // Rotate to north first
                val first = rotateVoxelShapeVertically(from, Direction.NORTH, shape)
                // Rotate to desired rotation
                return rotateVoxelShapeHorizontally(Direction.NORTH, to, first)
            } else if(to.horizontal == -1) {
                // Rotate to north first
                val first = rotateVoxelShapeHorizontally(from, Direction.NORTH, shape)
                // Rotate to desired rotation
                return rotateVoxelShapeVertically(Direction.NORTH, to, first)
            } else {
                // Horizontal rotation
                return rotateVoxelShapeHorizontally(from, to, shape)
            }
        }
    }
}