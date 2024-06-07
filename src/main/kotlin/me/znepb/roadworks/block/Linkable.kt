package me.znepb.roadworks.block

import me.znepb.roadworks.block.cabinet.TrafficCabinetBlockEntity
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class Linkable
    (
    pos: BlockPos,
    state: BlockState,
    blockEntityType: BlockEntityType<*>,
)
    : AbstractPostMountableBlockEntity(blockEntityType, pos, state) {
    protected var linked = false
    protected var linkX = 0
    protected var linkY = 0
    protected var linkZ = 0

    init {
        this.markDirty()
    }

    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: Linkable) {
            blockEntity.onTick(world, pos, state, blockEntity)
        }
    }

    fun getLinkPos() = BlockPos(linkX, linkY, linkZ)
    fun isLinked() = linked

    fun link(cabinetBlockEntity: TrafficCabinetBlockEntity): Int? {
        val id = cabinetBlockEntity.addDevice(this.pos)

        if (id != null) {
            linkX = cabinetBlockEntity.pos.x
            linkY = cabinetBlockEntity.pos.y
            linkZ = cabinetBlockEntity.pos.z
            linked = true

            this.markDirty()
            return id
        }

        return null
    }

    fun unlink() {
        linked = false
        reset()
        this.markDirty()
    }

    fun remove() {
        // Remove from cabinet when this block is removed
        if (linked) {
            val blockEntity = this.world?.getBlockEntity(getLinkPos())
            if (blockEntity is TrafficCabinetBlockEntity) {
                blockEntity.removeConnection(this.pos)
            }
        }
    }

    private fun reset() {
        this.linked = false
        this.markDirty()
    }

    override fun readExtraNBT(nbt: NbtCompound) {
        linked = nbt.getBoolean("linked")
        linkX = nbt.getInt("linkX")
        linkY = nbt.getInt("linkY")
        linkZ = nbt.getInt("linkZ")
    }

    override fun writeExtraNBT(nbt: NbtCompound) {
        nbt.putBoolean("linked", linked)
        nbt.putInt("linkX", linkX)
        nbt.putInt("linkY", linkY)
        nbt.putInt("linkZ", linkZ)
    }

    abstract fun getLinkType(): String

    fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: Linkable) {
        if (!world.isClient && blockEntity.isLinked() && world.getBlockEntity(
                BlockPos(
                    this.linkX,
                    this.linkY,
                    this.linkZ
                )
            ) !is TrafficCabinetBlockEntity
        ) {
            this.unlink()
        }

        super.onTick(world)
    }

    fun getLinkedCabinet(): TrafficCabinetBlockEntity? {
        val blockEntity = this.world?.getBlockEntity(BlockPos(this.linkX, this.linkY, this.linkZ))
        return if (blockEntity != null && blockEntity is TrafficCabinetBlockEntity) blockEntity else null
    }
}