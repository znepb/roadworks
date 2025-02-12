package me.znepb.roadworks.train

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.attachment.ActivatableAttachment
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.signal.BeaconAttachment
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.Blocks
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.EntityS2CPacket.Rotate
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import org.joml.Vector3d

class CrossingGateAttachment(container: AttachmentContainerBlockEntity) : ActivatableAttachment(RoadworksRegistry.ModAttachments.CROSSING_GATE, container) {
    private var extensionCount = 0
    private var progress = 0F
    private var lastProgress = 0F
    private var gateDirection = 1
    private var inMotion = false

    companion object {
        //val shape =
    }

    fun getShape(open: Boolean): VoxelShape  {
        val shape = VoxelShapes.union(
            VoxelShapes.cuboid(7.0 / 16.0, 5.0 / 16.0, 6.5 / 16.0, 9.0 / 16.0, 7.5 / 16.0, 9.0 / 16.0),
            VoxelShapes.cuboid(6.0 / 16.0, 0.0, 5.5 / 16.0, 10.0 / 16.0, 5 / 16.0, 10.0 / 16.0),
            VoxelShapes.cuboid(7.0 / 16.0, 7.5 / 16.0, 6.5 / 16.0, 12.75 / 16.0, 12 / 16.0, 8.5 / 16.0),
            VoxelShapes.cuboid(10.75 / 16.0, 12 / 16.0, 6.5 / 16.0, 12.75 / 16.0, 15 / 16.0, 8.5 / 16.0),
            VoxelShapes.cuboid(10.75 / 16.0, 15 / 16.0, 7 / 16.0, 12.75 / 16.0, 16 / 16.0, 8 / 16.0),
        )

        return if(open) RotateVoxelShape.rotateVoxelShape(RotateVoxelShape.rotateVoxelShape(shape, Direction.DOWN, Direction.NORTH), Direction.WEST, Direction.UP) else shape
    }

    override fun writeNBT(nbt: NbtCompound) {
        nbt.putBoolean("inMotion", inMotion)
        nbt.putInt("extensionCount", extensionCount)
        nbt.putFloat("progress", progress)
        nbt.putFloat("lastProgress", lastProgress)
        super.writeNBT(nbt)
    }

    override fun readNBT(nbt: NbtCompound) {
        if(nbt.getBoolean("inMotion") != this.isActive()) {
            activeChanged(!this.isActive())
        }

        this.inMotion = nbt.getBoolean("inMotion")
        this.extensionCount = nbt.getInt("extensionCount")
        this.progress = nbt.getFloat("progress")
        this.lastProgress = nbt.getFloat("lastProgress")

        super.readNBT(nbt)
    }

    override fun getLinkType() = "crossing_gate"

    override fun getShape(context: ShapeContext): VoxelShape {
        val depthOffset = this.container.getDepthOffset()

        return RotateVoxelShape.offsetFromDirectionXZ(
            RotateVoxelShape.rotateVoxelShape(getShape(this.isActive()), Direction.NORTH, if(this.isActive()) this.facing.opposite else this.facing),
            facing,
            Vector3d(0.0, 0.0, -depthOffset - BeaconAttachment.HALF),
            Vector3d(depthOffset + BeaconAttachment.HALF, 0.0, 0.0),
            Vector3d(0.0, 0.0, depthOffset + BeaconAttachment.HALF),
            Vector3d(-depthOffset - BeaconAttachment.HALF, 0.0, 0.0),
        )
    }

    fun getProgress(): Float {
        return progress
    }

    fun getProgress(tickDelta: Float): Float {
        if(!inMotion) {
            return if(isActive()) 0F else 1F
        }
        return MathHelper.lerp(tickDelta.coerceAtMost(1.0F), lastProgress, this.progress)
    }

    private fun removeExtensions() {
        val world = this.container.world

        if(world?.isClient == false) {
            val pos = this.container.pos
            val extensionDirection = if(isActive()) this.facing.rotateYClockwise() else Direction.UP

            for(i in 0..<extensionCount) {
                world.setBlockState(pos.offset(extensionDirection, i + 1), Blocks.AIR.defaultState)
            }
        }
    }

    private fun replaceExtensions() {
        val world = this.container.world

        if(world?.isClient == false) {
            val pos = this.container.pos
            val extensionDirection = if(isActive()) this.facing.rotateYClockwise() else Direction.UP

            for (i in 0..<extensionCount) {
                val position = pos.offset(extensionDirection, i + 1)
                if(world.getBlockState(position).isAir) {
                    world.setBlockState(
                        position,
                        RoadworksRegistry.ModBlocks.CROSSING_GATE_ARM_EXTENSION.defaultState
                            .with(
                                CrossingGateArmExtension.THICKNESS,
                                if (this.container is PostContainerBlockEntity) this.container.thickness else PostThickness.NONE
                            )
                            .with(
                                CrossingGateArmExtension.DIRECTION,
                                if (isActive()) CrossingGateArmExtension.CrossingArmDirection.HORIZONTAL else CrossingGateArmExtension.CrossingArmDirection.VERTICAL
                            )
                            .with(HorizontalFacingBlock.FACING, this.facing)
                    )
                } else {
                    val soundGroup = RoadworksRegistry.ModBlocks.CROSSING_GATE_ARM_EXTENSION.getSoundGroup(
                        RoadworksRegistry.ModBlocks.CROSSING_GATE_ARM_EXTENSION.defaultState
                    )
                    world.spawnEntity(ItemEntity(world, position.x.toDouble(), position.y.toDouble(), position.z.toDouble(), ItemStack(RoadworksRegistry.ModBlocks.CROSSING_GATE_ARM_EXTENSION)))
                    world.playSoundAtBlockCenter(position, soundGroup.breakSound, SoundCategory.BLOCKS, 1.0F, 0.75F, true)
                }
            }
        }
    }

    private fun countExtensions() {
        if(this.container.world?.isClient == true) return

        val extensionDirection = if(isActive()) this.facing.rotateYClockwise() else Direction.UP
        var hasExtension = true
        var extensions = 0

        val world = this.container.world
        val pos = this.container.pos

        if(world == null) return

        while(hasExtension) {
            val state = world.getBlockState(pos.offset(extensionDirection, extensions + 1))

            if(
                state.isOf(RoadworksRegistry.ModBlocks.CROSSING_GATE_ARM_EXTENSION)
                && state.get(HorizontalFacingBlock.FACING) == this.facing
            ) {
                extensions += 1
                hasExtension = true
            } else {
                hasExtension = false
            }
        }

        this.extensionCount = extensions
    }

    fun getExtensionCount() = extensionCount

    fun isInMotion() = inMotion

    override fun onTick() {
        if(!inMotion) return

        this.lastProgress = this.progress

        this.progress = (this.progress + (0.01F * gateDirection)).coerceAtMost(1F).coerceAtLeast(0F)

        val wasInMotion = inMotion
        inMotion = !(progress == 1F || progress == 0F)

        if(inMotion != wasInMotion) {
            replaceExtensions()
        }
    }

    override fun setActive(active: Boolean) {
        if(inMotion) return
        this.activeChanged(active)
        super.setActive(active)
    }

    private fun activeChanged(active: Boolean) {
        if(this.isActive() == active) return

        if(this.container.world?.isClient == false) {
            countExtensions()
            removeExtensions()
        }

        inMotion = true
        gateDirection = if(active) -1 else 1
        this.markDirty()
        this.lastProgress = if(gateDirection == 1) 0.0F else 1.0F
        this.progress = if(gateDirection == 1) 0.0F else 1.0F
    }
}