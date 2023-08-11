package me.znepb.zrm.block.entity.signals

import com.mojang.authlib.minecraft.client.MinecraftClient
import me.znepb.zrm.Main.logger
import me.znepb.zrm.Registry
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
    var red = false
    var yellow = false
    var green = false
    var linked = false
    var linkX = 0
    var linkY = 0
    var linkZ = 0

    init {
        this.markDirty()
    }

    fun unlink() {
        linked = false
        this.markDirty()
    }

    fun link(pos: BlockPos) {
        linkX = pos.x
        linkY = pos.y
        linkZ = pos.z
        linked = false
        this.markDirty()
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
    }

    override fun writeExtraNBT(nbt: NbtCompound) {
        nbt.putBoolean("red", red)
        nbt.putBoolean("yellow", yellow)
        nbt.putBoolean("green", green)
        nbt.putBoolean("linked", linked)
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