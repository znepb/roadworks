package me.znepb.roadworks.sign

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.attachment.PositionableAttachment
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.ShapeContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.joml.Vector3d

class RouteShieldAttachment(container: AttachmentContainerBlockEntity) : PositionableAttachment(RoadworksRegistry.ModAttachments.ROUTE_SHEILD, container) {
    var number: Int = 66

    override fun getShape(context: ShapeContext): VoxelShape {
        val depthOffset = this.container.getDepthOffset()

        return RotateVoxelShape.offsetFromDirectionXZ(
            RotateVoxelShape.rotateVoxelShape(SignAttachment.SIGN_SHAPE_WALL, Direction.NORTH, this.facing),
            facing,
            Vector3d(0.0, 0.0, -depthOffset),
            Vector3d(depthOffset, 0.0, 0.0,),
            Vector3d(0.0, 0.0, depthOffset),
            Vector3d(-depthOffset, 0.0, 0.0),
        )
    }

    override fun writeNBT(nbt: NbtCompound) {
        nbt.putInt("number", number)
        super.writeNBT(nbt)
    }

    override fun readNBT(nbt: NbtCompound) {
        super.readNBT(nbt)
        this.number = nbt.getInt("number")
    }

    override fun isPositionable() = true
}