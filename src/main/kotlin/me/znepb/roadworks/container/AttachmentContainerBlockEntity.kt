package me.znepb.roadworks.container

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.attachment.Attachment
import me.znepb.roadworks.util.PostThickness
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import org.joml.Vector3d
import java.util.*

abstract class AttachmentContainerBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(blockEntityType, pos, state) {
    var attachments = listOf<Attachment>()

    override fun toUpdatePacket() = BlockEntityUpdateS2CPacket.create(this)
    override fun toInitialChunkDataNbt() = this.createNbt()

    override fun writeNbt(nbt: NbtCompound) {
        val attachmentList = NbtList()
        this.attachments.forEach {
            val compound = NbtCompound()
            it.writeNBT(compound)
            attachmentList.add(compound)
        }

        nbt.put("attachments", attachmentList)
    }

    override fun readNbt(nbt: NbtCompound) {
        val serializedAttachments = nbt.getList("attachments", NbtElement.COMPOUND_TYPE.toInt())
        val attachments = this.attachments.toMutableList()
        val uuidsFound = mutableListOf<UUID>()

        // Check for new attachments
        for (i in 0 until serializedAttachments.size) {
            val attachment = serializedAttachments.getCompound(i)
            val type = Identifier(attachment.getString("id"))
            val uuid = UUID.fromString(attachment.getString("uuid"))
            uuidsFound.add(uuid)

            val existingAttachment = attachments.find { it.id == uuid }

            if(existingAttachment == null) {
                val attachmentType = RoadworksRegistry.ModAttachments.REGISTRY.get(type)

                if (attachmentType == null) {
                    RoadworksMain.logger.warn("PostContainerBlockEntity at ${this.pos} has unknown attachment with ID $type, ignoring")
                    continue
                }

                val attachmentObject = attachmentType.factory.create(this)
                attachmentObject.readNBT(attachment)

                attachments.add(attachmentObject)
            } else {
                attachments[attachments.indexOf(existingAttachment)].readNBT(attachment)
            }
        }

        // Remove attachments that no longer exist
        this.attachments.forEach {
            if(!uuidsFound.contains(it.id)) {
                attachments.remove(it)
            }
        }

        this.attachments = attachments
    }

    fun addAttachment(attachment: Attachment, facing: Direction) {
        val attachments = mutableListOf<Attachment>()
        val initialNBT = NbtCompound()
        attachment.writeNBT(initialNBT)
        initialNBT.putString("facing", facing.getName())
        attachment.readNBT(initialNBT)

        attachments.addAll(0, this.attachments)
        attachments.add(attachment)
        this.attachments = attachments.toList()
        this.markDirty()
    }

    fun removeAttachment(uuid: UUID) {
        val attachment = this.attachments.find {
            it.id == uuid
        }
        attachment?.remove()

        val newAttachments = mutableListOf<Attachment>()
        this.attachments.forEach {
            if(it != attachment) newAttachments.add(it)
        }

        this.attachments = newAttachments.toList()
        this.markDirty()
    }

    fun getAttachment(uuid: UUID): Attachment? {
        return attachments.find { it.id == uuid }
    }

    protected fun sendAttachmentUpdate() {
        this.attachments.forEach { it.containerUpdate() }
    }

    fun sendAttachmentNeighborUpdate(state: BlockState, world: World, pos: BlockPos, sourceBlock: Block, sourcePos: BlockPos, notify: Boolean) {
        this.attachments.forEach { it.containerNeighborUpdate(state, world, pos, sourceBlock, sourcePos, notify) }
    }

    fun getPlayerAttachmentLookingAt(player: PlayerEntity): Attachment? {
        val eyePos = player.getCameraPosVec(1.0F)
        val lookDirection = player.getRotationVec(1.0F)
        val reachVector = eyePos.add(lookDirection.multiply(4.0))

        val result = player.world.raycast(
            RaycastContext(
            eyePos,
            reachVector,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        )
        )

        if(result.type == HitResult.Type.BLOCK) {
            val relativeHitPos: Vec3d = result.pos.subtract(result.blockPos.x.toDouble(), result.blockPos.y.toDouble(), result.blockPos.z.toDouble())
            return this.getAttachmentHit(relativeHitPos, ShapeContext.absent())
        }

        return null
    }

    fun getAttachmentHit(relativeHitPos: Vec3d, shapeContext: ShapeContext): Attachment? {
        val validAttachments = mutableListOf<Attachment>()
        val smallIntersectionBox = Box(
            relativeHitPos.x - 0.005, relativeHitPos.y - 0.005, relativeHitPos.z - 0.005,
            relativeHitPos.x + 0.005, relativeHitPos.y + 0.005, relativeHitPos.z + 0.005
        )

        // Check attachments
        this.attachments.forEach {
            val shape = it.getShape(shapeContext)
            shape.boundingBoxes.forEach { it1 ->
                if(it1.intersects(smallIntersectionBox) && !validAttachments.contains(it)) {
                    validAttachments.add(it)
                }
            }
        }

        return if(validAttachments.size == 0) null else validAttachments[0]
    }

    fun getAttachmentHit(hit: BlockHitResult): Attachment? {
        val pos = hit.blockPos
        val relativeHit = hit.pos.subtract(
            pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()
        )

        return getAttachmentHit(relativeHit, ShapeContext.absent())
    }

    fun getAttachmentsOnFace(direction: Direction): List<Attachment> {
        return this.attachments.filter { it.facing == direction }
    }

    override fun markDirty() {
        this.world?.updateListeners(pos, this.cachedState, this.cachedState, Block.NOTIFY_LISTENERS)
        super.markDirty()
    }

    open fun onUse(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        return ActionResult.SUCCESS
    }

    abstract fun getDepthOffset(): Double

    fun remove() {
        this.attachments.forEach {
            it.remove()
        }
    }
}