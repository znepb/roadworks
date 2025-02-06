package me.znepb.roadworks.signal

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.attachment.AttachmentType
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import org.joml.Vector3d

open class FiveHeadSignalAttachment(signalType: SignalType, attachmentType: AttachmentType<*>, container: AttachmentContainerBlockEntity) : AbstractSignalAttachment(signalType, attachmentType, container) {
    companion object {
        val SIGNAL_SHAPE = VoxelShapes.union(
            BlockWithEntity.createCuboidShape(5.0, 10.0, 7.5, 11.0, 15.0, 8.5),
            BlockWithEntity.createCuboidShape(3.0, 1.0, 7.5, 13.0, 11.0, 8.5)
        )
        const val HALF = 0.03125
    }

    override fun getShape(context: ShapeContext): VoxelShape {
        val depthOffset = this.container.getDepthOffset()

        return RotateVoxelShape.offsetFromDirectionXZ(
            RotateVoxelShape.rotateVoxelShape(SIGNAL_SHAPE, Direction.NORTH, this.facing),
            facing,
            Vector3d(0.0, 0.0, -depthOffset - HALF),
            Vector3d(depthOffset + HALF, 0.0, 0.0),
            Vector3d(0.0, 0.0, depthOffset + HALF),
            Vector3d(-depthOffset - HALF, 0.0, 0.0),
        )
    }

    class BallRight(container: AttachmentContainerBlockEntity) : FiveHeadSignalAttachment(SignalType.FIVE_HEAD_RIGHT, RoadworksRegistry.ModAttachments.FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_RIGHT, container)
    class LeftBall(container: AttachmentContainerBlockEntity) : FiveHeadSignalAttachment(SignalType.FIVE_HEAD_LEFT, RoadworksRegistry.ModAttachments.FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT, container)
    class LeftRight(container: AttachmentContainerBlockEntity) : FiveHeadSignalAttachment(SignalType.FIVE_HEAD_LEFT_RIGHT, RoadworksRegistry.ModAttachments.FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT_RIGHT, container)
}