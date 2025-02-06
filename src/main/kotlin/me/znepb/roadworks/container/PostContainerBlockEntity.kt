package me.znepb.roadworks.container

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.RoadworksRegistry.ModBlocks.CATWALK
import me.znepb.roadworks.RoadworksRegistry.ModBlocks.POST_CONTAINER
import me.znepb.roadworks.util.PostThickness
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

open class PostContainerBlockEntity(pos: BlockPos, state: BlockState) : AttachmentContainerBlockEntity(RoadworksRegistry.ModBlockEntities.POST_CONTAINER_BLOCK_ENTITY, pos, state) {
    var thickness = PostThickness.MEDIUM
    var up = PostThickness.NONE
    var down = PostThickness.NONE
    var north = PostThickness.NONE
    var east = PostThickness.NONE
    var south = PostThickness.NONE
    var west = PostThickness.NONE
    var footer = false
    var stub = false
    private var initialNBTFetch = false

    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: PostContainerBlockEntity?) {
            blockEntity?.onTick(world)
        }
    }

    public override fun writeNbt(nbt: NbtCompound) {
        nbt.putBoolean("footer", footer)
        nbt.putString("thickness", thickness.name)
        nbt.putString("up", up.name)
        nbt.putString("down", down.name)
        nbt.putString("north", north.name)
        nbt.putString("east", east.name)
        nbt.putString("south", south.name)
        nbt.putString("west", west.name)
        nbt.putBoolean("stub", stub)

        super.writeNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        initialNBTFetch = true

        super.readNbt(nbt)

        footer = if(nbt.contains("footer")) nbt.getBoolean("footer") else this.footer
        thickness = PostThickness.fromName(nbt.getString("thickness"))
        up = PostThickness.fromNameNullable(nbt.getString("up")) ?: this.up
        down = PostThickness.fromNameNullable(nbt.getString("down")) ?: this.down
        north = PostThickness.fromNameNullable(nbt.getString("north")) ?: this.north
        east = PostThickness.fromNameNullable(nbt.getString("east")) ?: this.east
        south = PostThickness.fromNameNullable(nbt.getString("south")) ?: this.south
        west = PostThickness.fromNameNullable(nbt.getString("west")) ?: this.west
        stub = if(nbt.contains("stub")) nbt.getBoolean("stub")  && !this.footer else this.stub && !this.footer

        if(thickness == PostThickness.NONE) {
            thickness = PostThickness.MEDIUM
        }

        this.getWorld()?.getBlockState(this.getPos())?.updateNeighbors(this.world, this.pos, Block.NOTIFY_NEIGHBORS, 2)
    }

    private fun canCheckConnections(world: WorldAccess?): Boolean {
        val chunk = world?.chunkManager?.isChunkLoaded(pos.x / 16, pos.z / 16)
        val chunkN = world?.chunkManager?.isChunkLoaded(pos.x / 16, (pos.z / 16) - 1)
        val chunkE = world?.chunkManager?.isChunkLoaded((pos.x / 16) + 1, pos.z / 16)
        val chunkS = world?.chunkManager?.isChunkLoaded(pos.x / 16, (pos.z / 16) + 1)
        val chunkW = world?.chunkManager?.isChunkLoaded((pos.x / 16) - 1, pos.z / 16)

        return chunk == true && chunkN == true && chunkE == true && chunkS == true && chunkW == true
    }

    fun getConnections(world: WorldAccess) {
        if(canCheckConnections(world)) {
            val stateDown = world.getBlockState(pos.down())
            val stateUp = world.getBlockState(pos.up())
            val stateNorth = world.getBlockState(pos.north())
            val stateEast = world.getBlockState(pos.east())
            val stateSouth = world.getBlockState(pos.south())
            val stateWest = world.getBlockState(pos.west())

            footer = shouldBeFooter(stateDown)
            down = if(!footer) this.getConnectionThickness(pos.down(), stateDown, Direction.DOWN) else PostThickness.NONE
            up = this.getConnectionThickness(pos.up(), stateUp, Direction.UP)
            north = this.getConnectionThickness(pos.north(), stateNorth, Direction.NORTH)
            south = this.getConnectionThickness(pos.south(), stateSouth, Direction.SOUTH)
            east = this.getConnectionThickness(pos.east(), stateEast, Direction.EAST)
            west = this.getConnectionThickness(pos.west(), stateWest, Direction.WEST)
            stub = stub && up == PostThickness.NONE && north == PostThickness.NONE && south == PostThickness.NONE && east == PostThickness.NONE && west == PostThickness.NONE && down != PostThickness.NONE && !footer

            this.markDirty()
            this.sendAttachmentUpdate()
        }
    }

    private fun shouldBeFooter(state: BlockState?): Boolean {
        return state != null &&
                (!state.isOf(Blocks.AIR)
                        && !state.isOf(POST_CONTAINER))
    }

    private fun getConnectionThickness(pos: BlockPos, state: BlockState?, dir: Direction): PostThickness {
        if(state == null) return PostThickness.NONE

        if(state.isOf(POST_CONTAINER)) {
            // Don't attach to the front of blocks that are post-mountable
            val blockEntity = this.world?.getBlockEntity(this.pos.offset(dir))
            if(blockEntity is PostContainerBlockEntity) {
                return if(getAttachmentsOnFace(dir).isNotEmpty() || blockEntity.getAttachmentsOnFace(dir.opposite).isNotEmpty()) {
                    PostThickness.NONE
                } else {
                    blockEntity.thickness
                }
            }
        } else if(state.isOf(CATWALK) && dir === Direction.UP) {
            return this.thickness
        }

        val be = world?.getBlockEntity(pos)
        return if(be != null && be is PostContainerBlockEntity) be.thickness else PostThickness.NONE
    }

    fun getDirectionThickness(dir: Direction): PostThickness {
        return when(dir) {
            Direction.NORTH -> north
            Direction.EAST -> east
            Direction.SOUTH -> south
            Direction.WEST -> west
            Direction.UP -> up
            Direction.DOWN -> down
            else -> PostThickness.NONE
        }
    }

    fun onTick(world: World) {
        this.setWorld(world)
        this.attachments.forEach { it.onTick() }
    }

    fun isHorizontal() = this.north == PostThickness.NONE || this.east != PostThickness.NONE || this.south == PostThickness.NONE || this.west != PostThickness.NONE
    fun isVertical() = this.up != PostThickness.NONE || this.down != PostThickness.NONE || this.footer

    override fun onUse(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if(player.isHolding(RoadworksRegistry.ModItems.WRENCH)) {
            stub = !stub && (up == PostThickness.NONE && north == PostThickness.NONE && south == PostThickness.NONE && east == PostThickness.NONE && west == PostThickness.NONE && down != PostThickness.NONE && !footer)
            val result = if(up == PostThickness.NONE && north == PostThickness.NONE && south == PostThickness.NONE && east == PostThickness.NONE && west == PostThickness.NONE && down != PostThickness.NONE && !footer) ActionResult.SUCCESS else ActionResult.PASS
            if(result == ActionResult.SUCCESS) this.sendAttachmentUpdate()
            return result
        }

        return ActionResult.PASS
    }

    override fun getDepthOffset(): Double = this.thickness.thickness / 2
}