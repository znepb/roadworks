package me.znepb.zrm.block.entity

import me.znepb.zrm.datagen.TagProvider
import me.znepb.zrm.util.PostThickness
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

open class PostMountableBlockEntity(
    type: BlockEntityType<*>?,
    pos: BlockPos,
    state: BlockState
) : BlockEntity(type, pos, state) {
    var up = PostThickness.NONE.id
    var down = PostThickness.NONE.id
    var north = PostThickness.NONE.id
    var east = PostThickness.NONE.id
    var south = PostThickness.NONE.id
    var west = PostThickness.NONE.id
    var facing = Direction.NORTH.id
    var wall = false
    var ctx: ItemPlacementContext? = null

    var hasLoaded = false

    init {
        getPlacementState(null)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound? {
        return createNbt()
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

    private fun getConnectionInt(state: BlockState, entity: BlockEntity?, dir: Direction): PostThickness {
        if(facing == dir.id) return PostThickness.NONE
        val thickness = PostThickness.fromState(state)

        if(thickness != PostThickness.NONE) {
            return thickness
        } else if(state.isIn(TagProvider.POST_MOUNTABLES) && entity is PostMountableBlockEntity) {
            return getThickest(entity)
        }

        return PostThickness.NONE
    }

    private fun canConnect(state: BlockState?, entity: BlockEntity?, dir: Direction): PostThickness {
        if(state == null) return PostThickness.NONE

        return when (dir) {
            Direction.DOWN -> getConnectionInt(state, entity, dir)
            Direction.UP -> getConnectionInt(state, entity, dir)
            Direction.NORTH -> getConnectionInt(state, entity, dir)
            Direction.EAST -> getConnectionInt(state, entity, dir)
            Direction.SOUTH -> getConnectionInt(state, entity, dir)
            Direction.WEST -> getConnectionInt(state, entity, dir)
            else -> PostThickness.NONE
        }
    }

    fun setContext(ctx: ItemPlacementContext) {
        this.ctx = ctx
    }

    fun getPlacementState(ctx: ItemPlacementContext?) {
        var context = ctx

        val stateDown = world?.getBlockState(pos.down())
        val stateUp = world?.getBlockState(pos.up())
        val stateNorth = world?.getBlockState(pos.north())
        val stateEast = world?.getBlockState(pos.east())
        val stateSouth = world?.getBlockState(pos.south())
        val stateWest = world?.getBlockState(pos.west())

        val entityDown = world?.getBlockEntity(pos.down())
        val entityUp = world?.getBlockEntity(pos.up())
        val entityNorth = world?.getBlockEntity(pos.north())
        val entityEast = world?.getBlockEntity(pos.east())
        val entitySouth = world?.getBlockEntity(pos.south())
        val entityWest = world?.getBlockEntity(pos.west())

        if(ctx == null && this.ctx != null) context = this.ctx

        if(context != null) {
            val facing =  context.horizontalPlayerFacing.opposite
            val placedOnPos = pos.offset(facing.opposite)
            val placedOnState = world?.getBlockState(placedOnPos)
            var isWall = true

            Direction.values().forEach {
                if(it != facing.opposite && world?.getBlockState(pos.offset(it))?.isIn(TagProvider.POSTS) == true) {
                    isWall = false
                }
            }

            if(placedOnState?.isTransparent(world, placedOnPos) == true && !placedOnState.isIn(TagProvider.POSTS)) {
                isWall = false
            }

            this.facing = facing.id
            this.wall = isWall
            this.ctx = null
        }

        this.down = this.canConnect(stateDown, entityDown, Direction.DOWN).id
        this.up = this.canConnect(stateUp, entityUp, Direction.UP).id
        this.north = this.canConnect(stateNorth, entityNorth, Direction.NORTH).id
        this.east = this.canConnect(stateEast, entityEast, Direction.EAST).id
        this.south = this.canConnect(stateSouth, entitySouth, Direction.SOUTH).id
        this.west = this.canConnect(stateWest, entityWest, Direction.WEST).id

        this.markDirty()
        this.world?.updateListeners(pos, this.cachedState, this.cachedState, Block.NOTIFY_LISTENERS)
    }

    open fun writeExtraNBT(nbt: NbtCompound) {}

    public override fun writeNbt(nbt: NbtCompound) {
        nbt.putInt("up", up)
        nbt.putInt("down", down)
        nbt.putInt("north", north)
        nbt.putInt("east", east)
        nbt.putInt("south", south)
        nbt.putInt("west", west)
        nbt.putInt("facing", facing)
        nbt.putBoolean("wall", wall)
        writeExtraNBT(nbt)

        super.writeNbt(nbt)
    }

    open fun readExtraNBT(nbt: NbtCompound) {}

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        up = nbt.getInt("up")
        down = nbt.getInt("down")
        north = nbt.getInt("north")
        east = nbt.getInt("east")
        south = nbt.getInt("south")
        west = nbt.getInt("west")
        facing = nbt.getInt("facing")
        wall = nbt.getBoolean("wall")
        readExtraNBT(nbt)

        this.getPlacementState(null)
    }

    fun onTick(world: World) {
        val chunk = world.chunkManager.isChunkLoaded(pos.x / 16, pos.z / 16)
        val chunkN = world.chunkManager.isChunkLoaded(pos.x / 16, (pos.z / 16) - 1)
        val chunkE = world.chunkManager.isChunkLoaded((pos.x / 16) + 1, pos.z / 16)
        val chunkS = world.chunkManager.isChunkLoaded(pos.x / 16, (pos.z / 16) + 1)
        val chunkW = world.chunkManager.isChunkLoaded((pos.x / 16) - 1, pos.z / 16)

        if(chunk && chunkN && chunkE && chunkS && chunkW) {
            hasLoaded = false
            this.getPlacementState(null)
        }
    }

    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: PostMountableBlockEntity?) {
            blockEntity?.onTick(world)
        }

        fun getThickest(entity: PostMountableBlockEntity): PostThickness {
            val up = if (entity.facing == Direction.UP.id) 0 else entity.up
            val down = if (entity.facing == Direction.DOWN.id) 0 else entity.down
            val north = if (entity.facing == Direction.NORTH.id) 0 else entity.north
            val east = if (entity.facing == Direction.EAST.id) 0 else entity.east
            val south = if (entity.facing == Direction.SOUTH.id) 0 else entity.south
            val west = if (entity.facing == Direction.WEST.id) 0 else entity.west

            return PostThickness.fromId(
                up.coerceAtLeast(down)
                .coerceAtLeast(north)
                .coerceAtLeast(east)
                .coerceAtLeast(south)
                .coerceAtLeast(west)
            )
        }
    }
}