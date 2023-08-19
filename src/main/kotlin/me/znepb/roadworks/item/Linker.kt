package me.znepb.roadworks.item

import me.znepb.roadworks.Registry
import me.znepb.roadworks.block.cabinet.TrafficCabinetBlockEntity
import me.znepb.roadworks.block.signals.AbstractTrafficSignalBlockEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Linker(settings: Settings) : Item(settings) {
    var linking: BlockPos? = null
    var linkingWith: BlockEntityType<*>? = null

    companion object {
        val MAX_DEVICES = 16
        val MAX_SIGNAL_DISTANCE = 32.0
    }

    private fun getCabinet(blockEntity: AbstractTrafficSignalBlockEntity, context: ItemUsageContext): TrafficCabinetBlockEntity? {
        val be = context.world?.getBlockEntity(blockEntity.getLinkPos())
        return if(be is TrafficCabinetBlockEntity) { be } else null
    }

    private fun unlink(
        signal: AbstractTrafficSignalBlockEntity,
        cabinet: TrafficCabinetBlockEntity,
        context: ItemUsageContext)
    {
        signal.unlink()
        cabinet.removeSignal(context.blockPos)
        context.player?.sendMessage(Text.literal("Signal unlinked"), true)
    }

    private fun alreadyLinked(
        signal: AbstractTrafficSignalBlockEntity,
        cabinet: TrafficCabinetBlockEntity,
        context: ItemUsageContext
    ) {
        if(context.player?.isSneaking == true) {
            unlink(signal, cabinet, context)
        } else {
            context.player?.sendMessage(
                // Notify player this is already linked
                Text.literal(
                    "Block is already linked as ID " +
                            "${cabinet.getSignalIdentifierFromBlockPos(context.blockPos)}. " +
                            "Crouch-Right click to unlink."
                ),
                true
            )
        }
    }

    private fun linkCabinet(cabinet: TrafficCabinetBlockEntity, context: ItemUsageContext): ActionResult {
        if(cabinet.getTotalDevices() > MAX_DEVICES) {
            // Too many signals connected to this box!
            context.player?.sendMessage(Text.literal("There are too many devices connected to this box! Max is $MAX_DEVICES"), true)
            return ActionResult.SUCCESS
        }

        linking = context.blockPos
        linkingWith = Registry.ModBlockEntities.CABINET_BLOCK_ENTITY
        context.player?.sendMessage(Text.literal("Right-click a traffic signal to link to this cabinet"), true)

        return ActionResult.SUCCESS
    }

    private fun completeLinkToCabinet(block: BlockEntity, context: ItemUsageContext) {
        val linkedFrom = context.world.getBlockEntity(linking)

        if(linkedFrom !is TrafficCabinetBlockEntity) {
            // Traffic cabinet disappeared somehow
            context.player?.sendMessage(Text.literal("The cabinet is no longer there"), true)
            return
        }

        if(!linkedFrom.pos.isWithinDistance(block.pos, MAX_SIGNAL_DISTANCE)) {
            // Device is too far!
            context.player?.sendMessage(Text.literal("This device is too far! Max distance is $MAX_SIGNAL_DISTANCE blocks"), true)
            return
        }

        if(block is AbstractTrafficSignalBlockEntity) {
            val id = block.link(linkedFrom)

            if(id != null) {
                // Signal connected!
                context.player?.sendMessage(Text.literal("Signal successfully connected with ID $id"), true)
            } else {
                // Something funky happened to the signal
                context.player?.sendMessage(Text.literal("Could not link signal"), true)
            }
        } else {
            context.player?.sendMessage(Text.literal("Cabinet must be connected to a signal"), true)
            return
        }
    }

    private fun linkSignal(signal: AbstractTrafficSignalBlockEntity, context: ItemUsageContext): ActionResult {
        fun startLink() {
            linking = context.blockPos
            linkingWith = Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY
            context.player?.sendMessage(Text.literal("Right-click a traffic cabinet to link this signal"), true)
        }

        if (signal.isLinked()) {
            val cabinet = getCabinet(signal, context)
            if(cabinet != null) {
                alreadyLinked(signal, cabinet, context)
            } else {
                startLink()
            }
        } else {
            startLink()
        }

        return ActionResult.SUCCESS
    }

    private fun completeSignalToCabinetLink(cabinet: BlockEntity, context: ItemUsageContext) {
        if(cabinet !is TrafficCabinetBlockEntity) {
            // Player right-clicked on something other than a cabinet
            context.player?.sendMessage(Text.literal("Signal must be connected to a traffic cabinet"), true)
            return
        }

        if(cabinet.getTotalSignals() >= MAX_DEVICES) {
            // Too many signals connected to this box!
            context.player?.sendMessage(Text.literal("There are too many signals connected to this box! Max is $MAX_DEVICES"), true)
            return
        }

        val linkedFrom = context.world.getBlockEntity(linking)

        if(linkedFrom !is AbstractTrafficSignalBlockEntity) {
            // Traffic signal disappeared somehow
            context.player?.sendMessage(Text.literal("The signal is no longer there"), true)
            return
        }

        if(!linkedFrom.pos.isWithinDistance(cabinet.pos, MAX_SIGNAL_DISTANCE)) {
            // Signal is too far!
            context.player?.sendMessage(Text.literal("This signal is too far! Max distance is $MAX_SIGNAL_DISTANCE blocks"), true)
            return
        }

        val id = linkedFrom.link(cabinet)
        if(id != null) {
            // Signal connected!
            context.player?.sendMessage(Text.literal("Signal successfully connected with ID $id"), true)
        } else {
            // Something funky happened to the signal
            context.player?.sendMessage(Text.literal("Could not link signal"), true)
        }
    }

    private fun reset() {
        linking = null
        linkingWith = null
    }

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if(user?.isSneaking == true) {
            reset()
            user.sendMessage(Text.literal("Linking state reset"), true)
        }

        return super.use(world, user, hand)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if(context.world?.isClient == true) {
            return ActionResult.CONSUME
        } else {
            val blockEntity = context.world?.getBlockEntity(context.blockPos)

            if(linking == null) {
                if (blockEntity is AbstractTrafficSignalBlockEntity) {
                    return linkSignal(blockEntity, context)
                } else if(blockEntity is TrafficCabinetBlockEntity) {
                    return linkCabinet(blockEntity, context)
                } else {
                    context.player?.sendMessage(Text.literal("Right-click a signal or traffic cabinet"), true)
                }
            } else if(blockEntity != null) {
                if(linkingWith == Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY) {
                    completeSignalToCabinetLink(blockEntity, context)
                    reset()
                } else if(linkingWith == Registry.ModBlockEntities.CABINET_BLOCK_ENTITY) {
                    completeLinkToCabinet(blockEntity, context)
                    reset()
                }
            }

            return ActionResult.SUCCESS
        }
    }
}