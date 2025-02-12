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

open class FourHeadSignalAttachment(signalType: SignalType, attachmentType: AttachmentType<*>, container: AttachmentContainerBlockEntity) : AbstractSignalAttachment(signalType, attachmentType, container) {
    companion object {
        val SIGNAL_SHAPE = BlockWithEntity.createCuboidShape(5.0, -1.0, 7.5, 11.0, 17.0, 8.5)
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

    class FlashingYellowArrowSignal(container: AttachmentContainerBlockEntity) : FourHeadSignalAttachment(SignalType.FLASHING_YELLOW_ARROW_SIGNAL, RoadworksRegistry.ModAttachments.FLASHING_YELLOW_ARROW_SIGNAL_ATTACHMENT, container)
}