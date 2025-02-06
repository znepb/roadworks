package me.znepb.roadworks.attachment

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.util.PostThickness
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult

abstract class PositionableAttachment(
    type: AttachmentType<*>,
    container: AttachmentContainerBlockEntity,
) : Attachment(type, container) {
    var position = getFirstValid()

    private fun getValidPositions(): List<AttachmentPosition> {
        val positions = mutableListOf<AttachmentPosition>()

        if(container is PostContainerBlockEntity) {
            if (container.up != PostThickness.NONE) positions.add(AttachmentPosition.TOP)
            if (!container.stub) positions.add(AttachmentPosition.MIDDLE)
            if (container.down != PostThickness.NONE) positions.add(AttachmentPosition.BOTTOM)
        }

        return positions.toList()
    }

    private fun isPositionValid(position: AttachmentPosition): Boolean {
        return getValidPositions().contains(position)
    }

    private fun getFirstValid(): AttachmentPosition {
        return if(isPositionValid(AttachmentPosition.MIDDLE)) AttachmentPosition.MIDDLE
        else if(isPositionValid(AttachmentPosition.BOTTOM)) AttachmentPosition.BOTTOM
        else AttachmentPosition.TOP
    }

    private fun getNextValid(): AttachmentPosition {
        var canidate = this.position.next()
        while(!isPositionValid(canidate)) canidate = canidate.next()
        return canidate
    }

    override fun writeNBT(nbt: NbtCompound) {
        nbt.putString("position", position.getName())
        super.writeNBT(nbt)
    }

    override fun readNBT(nbt: NbtCompound) {
        super.readNBT(nbt)

        this.position = if(!nbt.contains("position"))
            getFirstValid()
        else
            AttachmentPosition.fromName(nbt.getString("position") ?: getFirstValid().position) ?: getFirstValid()
    }

    override fun containerUpdate() {
        if(!isPositionValid(this.position)) this.position = this.getFirstValid()
    }

    abstract fun isPositionable(): Boolean

    override fun onUse(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if(!isPositionable()) return ActionResult.PASS
        if(this.container is PostContainerBlockEntity && !this.container.isVertical()) return ActionResult.PASS

        if(player.isHolding(RoadworksRegistry.ModItems.WRENCH)) {
            val newPosition = getNextValid()
            return if(newPosition == position) ActionResult.PASS else {
                this.position = newPosition
                ActionResult.SUCCESS
            }
        }

        return super.onUse(player, hand, hit)
    }
}