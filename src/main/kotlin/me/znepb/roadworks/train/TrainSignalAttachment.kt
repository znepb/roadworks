package me.znepb.roadworks.train

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.attachment.LinkableAttachment
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.signal.BeaconAttachment
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.joml.Vector3d

class TrainSignalAttachment(container: AttachmentContainerBlockEntity) : LinkableAttachment(RoadworksRegistry.ModAttachments.TRAIN_SIGNAL, container) {
    override fun getLinkType() = "train_beacon"
    private var isActivated = true
    private var leftOn = false
    private var rightOn = false

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

    fun isRightOn() = rightOn
    fun isLeftOn() = leftOn

    override fun writeNBT(nbt: NbtCompound) {
        nbt.putBoolean("left", leftOn)
        nbt.putBoolean("right", rightOn)
        super.writeNBT(nbt)
    }

    override fun readNBT(nbt: NbtCompound) {
        super.readNBT(nbt)

        this.leftOn = if(nbt.contains("left")) nbt.getBoolean("left") else false
        this.rightOn = if(nbt.contains("right")) nbt.getBoolean("right") else false
    }

    fun isActive() = isActivated
    fun activate() { isActivated = true }
    fun deactivate() { isActivated = false }
    fun setActive(active: Boolean) {
        isActivated = active
    }

    override fun onTick() {
        if(this.container.world?.isClient == true) return

        val world = this.container.world
        val server = world?.server

        if(world != null && server != null) {
            if(this.isActivated && server.ticks % 12 == 0) {
                if(!leftOn && !rightOn) rightOn = true
                leftOn = !leftOn
                rightOn = !rightOn
                markDirty()
            } else if((leftOn || rightOn) && !this.isActivated) {
                leftOn = false
                rightOn = false
                markDirty()
            }
        }
    }
}