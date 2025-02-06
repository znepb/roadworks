package me.znepb.roadworks.attachment

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtFloat
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.joml.Vector3f
import java.util.*

abstract class Attachment(
    val type: AttachmentType<*>,
    val container: AttachmentContainerBlockEntity,
) {
    var facing: Direction = Direction.NORTH
    var offset: Vector3f = Vector3f(0F, 0F, 0F)
    var id: UUID = UUID.randomUUID()

    open fun readNBT(nbt: NbtCompound) {
        this.facing = Direction.byName(nbt.getString("facing")) ?: Direction.NORTH

        val offset = nbt.getList("offset", NbtList.FLOAT_TYPE.toInt())
        this.offset = Vector3f(offset.getFloat(0), offset.getFloat(1), offset.getFloat(2))
        if(nbt.contains("uuid", NbtCompound.STRING_TYPE.toInt())) {
            this.id = UUID.fromString(nbt.getString("uuid")) ?: UUID.randomUUID()
        }
    }

    open fun writeNBT(nbt: NbtCompound) {
        val typeIdentifier = RoadworksRegistry.ModAttachments.REGISTRY.getId(type)
        nbt.putString("id", typeIdentifier.toString())
        nbt.putString("facing", facing.getName())
        val offsetList = NbtList()
        offsetList.add(0, NbtFloat.of(offset.x))
        offsetList.add(1, NbtFloat.of(offset.x))
        offsetList.add(2, NbtFloat.of(offset.z))

        nbt.put("offset", offsetList)
        nbt.put("uuid", NbtString.of(id.toString()))
    }

    fun markDirty() {
        this.container.markDirty()
    }

    abstract fun getShape(context: ShapeContext): VoxelShape

    open fun remove() {}

    open fun onTick() {}

    open fun containerUpdate() {}

    open fun onUse(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult = ActionResult.PASS
}