package me.znepb.roadworks.sign

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.attachment.AttachmentPosition
import me.znepb.roadworks.attachment.PositionableAttachment
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.util.Charset
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.joml.Vector3d

class RoadSignAttachment(
    container: AttachmentContainerBlockEntity,
) : PositionableAttachment(RoadworksRegistry.ModAttachments.ROAD_SIGN_ATTACHMENT, container) {
    var color = "green"
    var contents = listOf<Charset>()

    private fun getContentsPixelWidth(): Float {
        var size = -1
        contents.forEach {
            size += (it.w + 1)
        }

        val pixelCount = size.toFloat()

        return (pixelCount + 8) / 4
    }

    override fun getShape(context: ShapeContext): VoxelShape {
        val width = this.getContentsPixelWidth()
        val depthOffset = this.container.getDepthOffset()
        val offsetHeight = when(this.position) {
            AttachmentPosition.TOP -> 0.75
            AttachmentPosition.MIDDLE -> 0.375
            AttachmentPosition.BOTTOM -> 0.0
        }

        val shape = BlockWithEntity.createCuboidShape(
            (8 - width / 2).toDouble(),
            0.0,
            7.5,
            (8 + width / 2).toDouble(),
            4.0,
            8.5
        ).offset(0.0, offsetHeight, 0.0)

        return RotateVoxelShape.offsetFromDirectionXZ(
            RotateVoxelShape.rotateVoxelShape(shape, Direction.NORTH, this.facing),
            facing,
            Vector3d(0.0, 0.0, -depthOffset),
            Vector3d(depthOffset, 0.0, 0.0,),
            Vector3d(0.0, 0.0, depthOffset),
            Vector3d(-depthOffset, 0.0, 0.0),
        )
    }

    override fun writeNBT(nbt: NbtCompound) {
        val contents = mutableListOf<Int>()
        this.contents.forEach {
            contents.add(it.ordinal)
        }

        nbt.putString("color", color)
        nbt.putIntArray("contents", contents)
        super.writeNBT(nbt)
    }

    override fun readNBT(nbt: NbtCompound) {
        super.readNBT(nbt)

        this.color = nbt.getString("color")
        val contents = nbt.getIntArray("contents")

        val newList = mutableListOf<Charset>()
        contents.forEach {
            newList.add(Charset.entries[it])
        }
        this.contents = newList
    }

    override fun isPositionable() = true
}