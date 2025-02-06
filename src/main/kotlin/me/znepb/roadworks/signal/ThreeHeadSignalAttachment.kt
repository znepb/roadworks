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
import org.joml.Vector3d

open class ThreeHeadSignalAttachment(signalType: SignalType, attachmentType: AttachmentType<*>, container: AttachmentContainerBlockEntity) : AbstractSignalAttachment(signalType, attachmentType, container) {
    companion object {
        val SIGNAL_SHAPE = BlockWithEntity.createCuboidShape(5.0, 1.0, 7.5, 11.0, 15.0, 8.5)
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

    class Ball(container: AttachmentContainerBlockEntity) : ThreeHeadSignalAttachment(SignalType.THREE_HEAD, RoadworksRegistry.ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT, container)
    class Left(container: AttachmentContainerBlockEntity) : ThreeHeadSignalAttachment(SignalType.THREE_HEAD_LEFT, RoadworksRegistry.ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT, container)
    class Right(container: AttachmentContainerBlockEntity) : ThreeHeadSignalAttachment(SignalType.THREE_HEAD_RIGHT, RoadworksRegistry.ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_RIGHT, container)
    class Straight(container: AttachmentContainerBlockEntity) : ThreeHeadSignalAttachment(SignalType.THREE_HEAD_STRAIGHT, RoadworksRegistry.ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_STRAIGHT, container)
}