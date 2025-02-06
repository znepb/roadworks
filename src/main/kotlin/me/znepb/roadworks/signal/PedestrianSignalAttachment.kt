package me.znepb.roadworks.signal

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.joml.Vector3d

class PedestrianSignalAttachment(container: AttachmentContainerBlockEntity) : AbstractSignalAttachment(
    SignalType.PEDESTRIAN, RoadworksRegistry.ModAttachments.PEDESTRIAN_SIGNAL, container
) {

    companion object {
        val SIGNAL_SHAPE = BlockWithEntity.createCuboidShape(4.0, 4.0, 6.5, 12.0, 12.0, 9.5)
    }

    override fun getShape(context: ShapeContext): VoxelShape {
        val depthOffset = this.container.getDepthOffset()

        return RotateVoxelShape.offsetFromDirectionXZ(
            RotateVoxelShape.rotateVoxelShape(SIGNAL_SHAPE, Direction.NORTH, this.facing),
            facing,
            Vector3d(0.0, 0.0, -depthOffset - BeaconAttachment.HALF),
            Vector3d(depthOffset + BeaconAttachment.HALF, 0.0, 0.0),
            Vector3d(0.0, 0.0, depthOffset + BeaconAttachment.HALF),
            Vector3d(-depthOffset - BeaconAttachment.HALF, 0.0, 0.0),
        )
    }
}