package me.znepb.roadworks.train

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.attachment.ActivatableAttachment
import me.znepb.roadworks.attachment.LinkableAttachment
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.signal.BeaconAttachment
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.joml.Vector3d

class TrainBellAttachment(container: AttachmentContainerBlockEntity) : ActivatableAttachment(RoadworksRegistry.ModAttachments.TRAIN_BELL, container) {
    override fun getLinkType() = "train_bell"

    private val shape = BlockWithEntity.createCuboidShape(6.0, 6.0, 7.5, 10.0, 10.0, 8.5)

    override fun getShape(context: ShapeContext): VoxelShape {
        val depthOffset = this.container.getDepthOffset()

        return RotateVoxelShape.offsetFromDirectionXZ(
            RotateVoxelShape.rotateVoxelShape(shape, Direction.NORTH, this.facing),
            facing,
            Vector3d(0.0, 0.0, -depthOffset - BeaconAttachment.HALF),
            Vector3d(depthOffset + BeaconAttachment.HALF, 0.0, 0.0),
            Vector3d(0.0, 0.0, depthOffset + BeaconAttachment.HALF),
            Vector3d(-depthOffset - BeaconAttachment.HALF, 0.0, 0.0),
        )
    }

    override fun onTick() {
        if(!this.isActive()) return
        if(this.container.world?.isClient == true) return

        val world = this.container.world
        val server = world?.server

        if(world != null && server != null) {
            if(server.ticks % 6 == 0) {
                world.playSound(null, this.container.pos, RoadworksRegistry.ModSounds.BELL_SOUND_EVENT, SoundCategory.BLOCKS, 3.0f, 1f)
            }
        }
    }
}