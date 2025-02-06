package me.znepb.roadworks.signal

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.attachment.AttachmentPosition
import me.znepb.roadworks.attachment.AttachmentType
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.joml.Vector3d

open class BeaconAttachment(signalType: SignalType, attachmentType: AttachmentType<*>, container: AttachmentContainerBlockEntity, )
    : AbstractSignalAttachment(signalType, attachmentType, container) {
    companion object {
        val SIGNAL_SHAPE = BlockWithEntity.createCuboidShape(5.0, 5.0, 7.5, 11.0, 11.0, 8.5)
        const val HALF = 0.03125
    }

    var position = AttachmentPosition.MIDDLE

    override fun getShape(context: ShapeContext): VoxelShape {
        val depthOffset = this.container.getDepthOffset()
        val offsetHeight = when(this.position) {
            AttachmentPosition.TOP -> 0.275
            AttachmentPosition.MIDDLE -> 0.0
            AttachmentPosition.BOTTOM -> -0.275
        }

        return RotateVoxelShape.offsetFromDirectionXZ(
            RotateVoxelShape.rotateVoxelShape(SIGNAL_SHAPE, Direction.NORTH, this.facing),
            facing,
            Vector3d(0.0, offsetHeight, -depthOffset - HALF),
            Vector3d(depthOffset + HALF, offsetHeight, 0.0),
            Vector3d(0.0, offsetHeight, depthOffset + HALF),
            Vector3d(-depthOffset - HALF, offsetHeight, 0.0),
        )
    }

    override fun writeNBT(nbt: NbtCompound) {
        nbt.putString("position", position.getName())
        super.writeNBT(nbt)
    }

    override fun readNBT(nbt: NbtCompound) {
        super.readNBT(nbt)

        this.position = if(this.container is PostContainerBlockEntity && !this.container.isVertical())
            AttachmentPosition.MIDDLE
        else
            AttachmentPosition.fromName(nbt.getString("position") ?: "middle") ?: AttachmentPosition.MIDDLE
    }

    override fun onUse(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if(this.container is PostContainerBlockEntity && !this.container.isVertical()) return ActionResult.PASS

        if(player.isHolding(RoadworksRegistry.ModItems.WRENCH)) {
            position = if(player.isSneaking) position.previous() else position.next()
            return ActionResult.SUCCESS
        }


        return super.onUse(player, hand, hit)
    }

    class Red(container: AttachmentContainerBlockEntity) : BeaconAttachment(SignalType.BEACON_RED, RoadworksRegistry.ModAttachments.BEACON_RED, container)
    class Yellow(container: AttachmentContainerBlockEntity) : BeaconAttachment(SignalType.BEACON_YELLOW, RoadworksRegistry.ModAttachments.BEACON_YELLOW, container)
    class Green(container: AttachmentContainerBlockEntity) : BeaconAttachment(SignalType.BEACON_GREEN, RoadworksRegistry.ModAttachments.BEACON_GREEN, container)
}