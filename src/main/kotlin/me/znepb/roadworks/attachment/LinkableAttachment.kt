package me.znepb.roadworks.attachment

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.cabinet.TrafficCabinetBlockEntity
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.util.MiscUtils.blockPosFromNbtIntArray
import me.znepb.roadworks.util.MiscUtils.blockPosToNbtIntArray
import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class LinkableAttachment(type: AttachmentType<*>, container: AttachmentContainerBlockEntity) : Attachment(type, container) {
    var linked = false
    var linkPosition = BlockPos(0, 0, 0)

    fun link(cabinet: TrafficCabinetBlockEntity): Int? {
        val id = cabinet.addDevice(this.container.pos, this.id)

        if(id != null) {
            linkPosition = cabinet.pos
            linked = true
            this.markDirty()
            return id
        }

        return null
    }

    fun getLinkedCabinet(): TrafficCabinetBlockEntity? {
        val be = this.container.world?.getBlockEntity(this.linkPosition, RoadworksRegistry.ModBlockEntities.CABINET_BLOCK_ENTITY)
        return if(be != null && be.isPresent) be.get() else null
    }

    abstract fun getLinkType(): String

    fun unlink() {
        if(linked) {
            getLinkedCabinet()?.removeConnection(this.linkPosition, this.id)
        }
        linked = false
        this.markDirty()
    }

    fun checkLinkExists() {
        if(this.container.world?.isChunkLoaded(linkPosition.x / 16, linkPosition.z / 16) == false) return
        val cabinet = getLinkedCabinet()
        if(getLinkedCabinet() == null) {
            this.unlink()
        }
    }

    override fun readNBT(nbt: NbtCompound) {
        super.readNBT(nbt)
        linked = if(nbt.contains("linked")) nbt.getBoolean("linked") else false
        linkPosition = if(nbt.contains("linkPosition") )blockPosFromNbtIntArray(nbt.getIntArray("linkPosition")) else BlockPos.ORIGIN
    }

    override fun writeNBT(nbt: NbtCompound) {
        super.writeNBT(nbt)
        nbt.putBoolean("linked", linked)
        nbt.putIntArray("linkPosition", blockPosToNbtIntArray(this.linkPosition))
    }

    fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: LinkableAttachment) {
        this.checkLinkExists()
    }
}