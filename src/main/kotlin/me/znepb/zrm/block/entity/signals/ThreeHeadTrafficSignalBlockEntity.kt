package me.znepb.zrm.block.entity.signals

import com.mojang.authlib.minecraft.client.MinecraftClient
import me.znepb.zrm.Main.logger
import me.znepb.zrm.Registry
import me.znepb.zrm.block.cabinet.TrafficCabinetBlockEntity
import me.znepb.zrm.block.entity.PostMountableBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class ThreeHeadTrafficSignalBlockEntity(pos: BlockPos, state: BlockState)
    : PostMountableBlockEntity(Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY, pos, state)
{
    private var red = false
    private var yellow = false
    private var green = false
    private var linked = false
    private var linkX = 0
    private var linkY = 0
    private var linkZ = 0

    init {
        this.markDirty()
    }

    fun unlink() {
        linked = false
        red = false
        yellow = false
        green = false
        this.markDirty()
    }

    fun getLinkPos() = BlockPos(linkX, linkY, linkZ)
    fun isLinked() = linked
    fun setRed(on: Boolean) {
        red = on
        markDirty()
    }
    fun setYellow(on: Boolean) {
        yellow = on
        markDirty()
    }
    fun setGreen(on: Boolean) {
        green = on
        markDirty()
    }
    fun getRed() = red
    fun getYellow() = yellow
    fun getGreen() = green

    fun link(cabinetBlockEntity: TrafficCabinetBlockEntity): Int? {
        val id = cabinetBlockEntity.addThreeHeadSignal(this.pos)

        if(id != null) {
            linkX = cabinetBlockEntity.pos.x
            linkY = cabinetBlockEntity.pos.y
            linkZ = cabinetBlockEntity.pos.z
            linked = true

            this.markDirty()

            return id
        }

        return null
    }

    fun remove() {
        // Remove from cabinet when this block is removed
        if(linked) {
            val blockEntity = this.world?.getBlockEntity(getLinkPos())
            if(blockEntity is TrafficCabinetBlockEntity) {
                blockEntity.removeThreeHeadSignal(this.pos)
            }
        }
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound? {
        return createNbt()
    }

    override fun readExtraNBT(nbt: NbtCompound) {
        red = nbt.getBoolean("red")
        yellow = nbt.getBoolean("yellow")
        green = nbt.getBoolean("green")
        linked = nbt.getBoolean("linked")
        linkX = nbt.getInt("linkX")
        linkY = nbt.getInt("linkX")
        linkZ = nbt.getInt("linkX")
    }

    override fun writeExtraNBT(nbt: NbtCompound) {
        nbt.putBoolean("red", red)
        nbt.putBoolean("yellow", yellow)
        nbt.putBoolean("green", green)
        nbt.putBoolean("linked", linked)
        nbt.putInt("linkX", linkX)
        nbt.putInt("linkY", linkY)
        nbt.putInt("linkZ", linkZ)
    }

    fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: ThreeHeadTrafficSignalBlockEntity?) {
        if(blockEntity?.linked == false && !world.isClient) {
            val ticks = world.server?.ticks
            if(ticks != null) {
                this.red = (ticks % 40) > 20;
            }
        }
        super.onTick(world)
    }

    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: ThreeHeadTrafficSignalBlockEntity?) {
            blockEntity?.onTick(world, pos, state, blockEntity)
        }
    }
}