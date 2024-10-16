package me.znepb.roadworks.block.post

import me.znepb.roadworks.Registry
import me.znepb.roadworks.datagen.TagProvider
import me.znepb.roadworks.util.PostThickness
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

    private fun getConnectionThickness(state: BlockState, dir: Direction): PostThickness {
        if(state.isIn(TagProvider.POST_MOUNTABLES)) {
            // Don't attach to the front of blocks that are post-mountable
            val blockEntity = this.world?.getBlockEntity(this.pos.offset(dir))
            if(blockEntity is AbstractPostMountableBlockEntity) {
                return if(Direction.byId(blockEntity.facing).equals(dir.opposite)) {
                    PostThickness.NONE
                } else {
                    AbstractPostMountableBlockEntity.getThickest(blockEntity)
                }
            }
        }

        return PostThickness.fromState(state)
    }

    fun getDirectionThickness(dir: Direction): PostThickness {
        return when(dir) {
            Direction.NORTH -> PostThickness.fromId(north)
            Direction.EAST -> PostThickness.fromId(east)
            Direction.SOUTH -> PostThickness.fromId(south)
            Direction.WEST -> PostThickness.fromId(west)
            Direction.UP -> PostThickness.fromId(up)
            Direction.DOWN -> PostThickness.fromId(down)
            else -> PostThickness.NONE
        }
    }

    private fun shouldBeFooter(state: BlockState?): Boolean {
        return state != null &&
                (!state.isOf(Blocks.AIR)
                        && !state.isIn(TagProvider.POSTS)
                        && !state.isIn(TagProvider.POST_MOUNTABLES))
    }

    private fun canConnect(state: BlockState?, dir: Direction): PostThickness {
        if(state == null) return PostThickness.NONE

        return when(dir) {
            Direction.DOWN -> getConnectionThickness(state, dir)
            Direction.UP -> getConnectionThickness(state, dir)
            Direction.NORTH -> getConnectionThickness(state, dir)
            Direction.EAST -> getConnectionThickness(state, dir)
            Direction.SOUTH -> getConnectionThickness(state, dir)
            Direction.WEST -> getConnectionThickness(state, dir)
            else -> PostThickness.NONE
        }
    }
    fun getPlacementState(pos: BlockPos) {
        val stateDown = this.world?.getBlockState(pos.down())
        val stateUp = this.world?.getBlockState(pos.up())
        val stateNorth = this.world?.getBlockState(pos.north())
        val stateEast = this.world?.getBlockState(pos.east())
        val stateSouth = this.world?.getBlockState(pos.south())
        val stateWest = this.world?.getBlockState(pos.west())

        footer = shouldBeFooter(stateDown)
        down = if(!footer) this.canConnect(stateDown, Direction.DOWN).id else PostThickness.NONE.id
        up = this.canConnect(stateUp, Direction.UP).id
        north = this.canConnect(stateNorth, Direction.NORTH).id
        south = this.canConnect(stateSouth, Direction.SOUTH).id
        east = this.canConnect(stateEast, Direction.EAST).id
        west = this.canConnect(stateWest, Direction.WEST).id

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
            this.getPlacementState(pos)
        }
    }
    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: PostBlockEntity?) {
            blockEntity?.onTick(world)
        }
    }
}