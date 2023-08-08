package me.znepb.zrm.block

import me.znepb.zrm.Main.logger
import me.znepb.zrm.Registry
import me.znepb.zrm.datagen.TagProvider
import me.znepb.zrm.datagen.TagProvider.Companion.POSTS
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

open class SignBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, pos, state) {
    var up = 0
    var down = 0
    var north = 0
    var east = 0
    var south = 0
    var west = 0
    var signFacing = 2
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

    private fun getConnectionInt(state: BlockState, entity: BlockEntity?, dir: Direction): Int {
        if(signFacing == dir.id) return 0

        if (state.isOf(Registry.ModBlocks.THICK_POST)) {
            return 3
        } else if(state.isOf(Registry.ModBlocks.POST)) {
            return 2
        } else if(state.isOf(Registry.ModBlocks.THIN_POST)) {
            return 1
        }

        if(state.isIn(TagProvider.SIGNS) && entity is SignBlockEntity) {
            return getThickest(entity)
        }

        return 0
    }

    private fun canConnect(state: BlockState?, entity: BlockEntity?, dir: Direction): Int {
        if(state == null) return 0

        return when (dir) {
            Direction.DOWN -> getConnectionInt(state, entity, dir)
            Direction.UP -> getConnectionInt(state, entity, dir)
            Direction.NORTH -> getConnectionInt(state, entity, dir)
            Direction.EAST -> getConnectionInt(state, entity, dir)
            Direction.SOUTH -> getConnectionInt(state, entity, dir)
            Direction.WEST -> getConnectionInt(state, entity, dir)
            else -> 0
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
            logger.info("Placing with context")
            val facing =  context.horizontalPlayerFacing.opposite
            val placedOnPos = pos.offset(facing.opposite)
            val placedOnState = world?.getBlockState(placedOnPos)
            var isWall = true

            Direction.values().forEach {
                if(it != facing.opposite && world?.getBlockState(pos.offset(it))?.isIn(POSTS) == true) {
                    isWall = false
                }
            }

            if(placedOnState?.isTransparent(world, placedOnPos) == true && !placedOnState.isIn(POSTS)) {
                isWall = false
            }

            this.signFacing = facing.id
            this.wall = isWall
            this.ctx = null
        }

        this.down = this.canConnect(stateDown, entityDown, Direction.DOWN)
        this.up = this.canConnect(stateUp, entityUp, Direction.UP)
        this.north = this.canConnect(stateNorth, entityNorth, Direction.NORTH)
        this.east = this.canConnect(stateEast, entityEast, Direction.EAST)
        this.south = this.canConnect(stateSouth, entitySouth, Direction.SOUTH)
        this.west = this.canConnect(stateWest, entityWest, Direction.WEST)
        this.markDirty()

        this.world?.updateListeners(pos, this.cachedState, this.cachedState, Block.NOTIFY_LISTENERS)
    }

    public override fun writeNbt(nbt: NbtCompound) {
        nbt.putInt("up", up)
        nbt.putInt("down", down)
        nbt.putInt("north", north)
        nbt.putInt("east", east)
        nbt.putInt("south", south)
        nbt.putInt("west", west)
        nbt.putInt("facing", signFacing)
        nbt.putBoolean("wall", wall)

        super.writeNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        up = nbt.getInt("up")
        down = nbt.getInt("down")
        north = nbt.getInt("north")
        east = nbt.getInt("east")
        south = nbt.getInt("south")
        west = nbt.getInt("west")
        signFacing = nbt.getInt("facing")
        wall = nbt.getBoolean("wall")

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
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: SignBlockEntity?) {
            blockEntity?.onTick(world);
        }

        fun getThickest(entity: SignBlockEntity): Int {
            return entity.down.coerceAtLeast(entity.up)
                .coerceAtLeast(entity.north)
                .coerceAtLeast(entity.east)
                .coerceAtLeast(entity.south)
                .coerceAtLeast(entity.west)
        }
    }
}