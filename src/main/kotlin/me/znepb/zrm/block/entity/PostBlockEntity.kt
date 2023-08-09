package me.znepb.zrm.block.entity

import me.znepb.zrm.Registry
import me.znepb.zrm.datagen.TagProvider
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

open class PostBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(Registry.ModBlockEntities.POST_BLOCK_ENTITY, pos, state) {
    var up = 0
    var down = 0
    var north = 0
    var east = 0
    var south = 0
    var west = 0
    var footer = false

    init {
        getPlacementState(pos)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound? {
        return createNbt()
    }

    private fun getConnectionInt(state: BlockState, dir: Direction): Int {
        if(state.isIn(TagProvider.SIGNS)) {
            return when(state.block) {
                Registry.ModBlocks.THICK_POST -> 3
                Registry.ModBlocks.POST -> 2
                Registry.ModBlocks.THIN_POST -> 1
                else -> 3
            }
        }

        if (state.isOf(Registry.ModBlocks.THICK_POST)) {
            return 3
        } else if(state.isOf(Registry.ModBlocks.POST)) {
            return 2
        } else if(state.isOf(Registry.ModBlocks.THIN_POST)) {
            return 1
        }

        return 0
    }

    private fun canConnect(state: BlockState?, dir: Direction): Int {
        if(state == null) return 0

        return when(dir) {
            Direction.DOWN -> {
                val connectionInt = getConnectionInt(state, dir)

                if(connectionInt > 0) {
                    return connectionInt
                } else if(state.isOf(Blocks.AIR)) {
                    return 0 // floating
                } else {
                    return 4 // on ground
                }
            }
            Direction.UP -> getConnectionInt(state, dir)
            Direction.NORTH -> getConnectionInt(state, dir)
            Direction.EAST -> getConnectionInt(state, dir)
            Direction.SOUTH -> getConnectionInt(state, dir)
            Direction.WEST -> getConnectionInt(state, dir)
            else -> 0
        }
    }
    fun getPlacementState(pos: BlockPos) {
        val stateDown = this.world?.getBlockState(pos.down())
        val stateUp = this.world?.getBlockState(pos.up())
        val stateNorth = this.world?.getBlockState(pos.north())
        val stateEast = this.world?.getBlockState(pos.east())
        val stateSouth = this.world?.getBlockState(pos.south())
        val stateWest = this.world?.getBlockState(pos.west())


        val downConn = this.canConnect(stateDown, Direction.DOWN)
        footer = downConn == 4
        down = downConn

        up = this.canConnect(stateUp, Direction.UP)
        north = this.canConnect(stateNorth, Direction.NORTH)
        south = this.canConnect(stateSouth, Direction.SOUTH)
        east = this.canConnect(stateEast, Direction.EAST)
        west = this.canConnect(stateWest, Direction.WEST)
        this.markDirty()

        this.world?.updateListeners(pos, this.cachedState, this.cachedState, Block.NOTIFY_LISTENERS)
    }

    public override fun writeNbt(nbt: NbtCompound) {
        nbt.putBoolean("footer", footer)
        nbt.putInt("up", up)
        nbt.putInt("down", down)
        nbt.putInt("north", north)
        nbt.putInt("east", east)
        nbt.putInt("south", south)
        nbt.putInt("west", west)

        super.writeNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        footer = nbt.getBoolean("footer")
        up = nbt.getInt("up")
        down = nbt.getInt("down")
        north = nbt.getInt("north")
        east = nbt.getInt("east")
        south = nbt.getInt("south")
        west = nbt.getInt("west")

        this.getPlacementState(pos)
    }

    fun onTick(world: World) {
        val chunk = world.chunkManager.isChunkLoaded(pos.x / 16, pos.z / 16)
        val chunkN = world.chunkManager.isChunkLoaded(pos.x / 16, (pos.z / 16) - 1)
        val chunkE = world.chunkManager.isChunkLoaded((pos.x / 16) + 1, pos.z / 16)
        val chunkS = world.chunkManager.isChunkLoaded(pos.x / 16, (pos.z / 16) + 1)
        val chunkW = world.chunkManager.isChunkLoaded((pos.x / 16) - 1, pos.z / 16)

        if(chunk && chunkN && chunkE && chunkS && chunkW) {
            this.getPlacementState(pos);
        }
    }
    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: PostBlockEntity?) {
            blockEntity?.onTick(world);
        }
    }
}