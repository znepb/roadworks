package me.znepb.roadworks.signal

import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.attachment.ActivatableAttachment
import me.znepb.roadworks.attachment.AttachmentType
import me.znepb.roadworks.attachment.LinkableAttachment
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.util.RotateVoxelShape
import me.znepb.roadworks.util.ShapeSized
import net.minecraft.block.ShapeContext
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.joml.Vector3d

open class BlankoutAttachment(val texture: Identifier, val isRectangular: Boolean, type: AttachmentType<*>, container: AttachmentContainerBlockEntity) : ActivatableAttachment(type, container) {
    override fun getLinkType() = "blankout"

    val squareShape = ShapeSized.createShape(2.0, 2.0, 6.5, 12.0, 12.0, 3.0)
    val rectangleShape = ShapeSized.createShape(2.0, 1.0, 6.5, 12.0, 14.0, 3.0)

    override fun getShape(context: ShapeContext): VoxelShape {
        val depthOffset = this.container.getDepthOffset()
        val shape = if(isRectangular) rectangleShape else squareShape

        return RotateVoxelShape.offsetFromDirectionXZ(
            RotateVoxelShape.rotateVoxelShape(shape, Direction.NORTH, this.facing),
            facing,
            Vector3d(0.0, 0.0, -depthOffset - BeaconAttachment.HALF),
            Vector3d(depthOffset + BeaconAttachment.HALF, 0.0, 0.0),
            Vector3d(0.0, 0.0, depthOffset + BeaconAttachment.HALF),
            Vector3d(-depthOffset - BeaconAttachment.HALF, 0.0, 0.0),
        )
    }

    class NoLeftTurn(container: AttachmentContainerBlockEntity) : BlankoutAttachment(ModId("signals/blankout_no_left_turn"), false, RoadworksRegistry.ModAttachments.BLANKOUT_NO_LEFT_TURN, container)
    class NoRightTurn(container: AttachmentContainerBlockEntity) : BlankoutAttachment(ModId("signals/blankout_no_right_turn"), false, RoadworksRegistry.ModAttachments.BLANKOUT_NO_RIGHT_TURN, container)
    class NoTurnOnRed(container: AttachmentContainerBlockEntity) : BlankoutAttachment(ModId("signals/blankout_no_turn_on_red"), true, RoadworksRegistry.ModAttachments.BLANKOUT_NO_TURN_ON_RED, container)
    class NoLeftTurnTrain(container: AttachmentContainerBlockEntity) : BlankoutAttachment(ModId("signals/blankout_no_left_turn_train"), true, RoadworksRegistry.ModAttachments.BLANKOUT_NO_LEFT_TURN_TRAIN, container)
    class NoRightTurnTrain(container: AttachmentContainerBlockEntity) : BlankoutAttachment(ModId("signals/blankout_no_right_turn_train"), true, RoadworksRegistry.ModAttachments.BLANKOUT_NO_RIGHT_TURN_TRAIN, container)
}