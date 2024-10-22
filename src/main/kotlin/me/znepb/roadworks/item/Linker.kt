package me.znepb.roadworks.item

import me.znepb.roadworks.Registry
import me.znepb.roadworks.block.Linkable
import me.znepb.roadworks.block.cabinet.TrafficCabinetBlockEntity
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
        const val MAX_DEVICES = 24
        val MAX_DEVICE_DISTANCE = 48.0
    }

    private fun getCabinet(blockEntity: Linkable, context: ItemUsageContext): TrafficCabinetBlockEntity? {
        val be = context.world?.getBlockEntity(blockEntity.getLinkPos())
        return if(be is TrafficCabinetBlockEntity) { be } else null
    }

    private fun unlink(
        device: Linkable,
        cabinet: TrafficCabinetBlockEntity,
        context: ItemUsageContext)
    {
        device.unlink()
        cabinet.removeConnection(context.blockPos)
        context.player?.sendMessage(Text.literal("Device unlinked"), true)
    }

    private fun alreadyLinked(
        device: Linkable,
        cabinet: TrafficCabinetBlockEntity,
        context: ItemUsageContext
    ) {
        if(context.player?.isSneaking == true) {
            unlink(device, cabinet, context)
        } else {
            context.player?.sendMessage(
                // Notify player this is already linked
                Text.literal(
                    "Device is already linked as ID " +
                            "${cabinet.getConnectionIdentifierFromBlockPos(context.blockPos)}. " +
                            "Crouch-Right click to unlink."
                ),
                true
            )
        }
    }

    private fun linkCabinet(cabinet: TrafficCabinetBlockEntity, context: ItemUsageContext): ActionResult {
        if(cabinet.getTotalDevices() > MAX_DEVICES) {
            // Too many devices connected to this box!
            context.player?.sendMessage(Text.literal("There are too many devices connected to this box! Max is $MAX_DEVICES"), true)
            return ActionResult.SUCCESS
        }

        linking = context.blockPos
        linkingWith = Registry.ModBlockEntities.CABINET_BLOCK_ENTITY
        context.player?.sendMessage(Text.literal("Right-click a traffic device to link to this cabinet"), true)

        return ActionResult.SUCCESS
    }

    private fun completeLinkToCabinet(block: BlockEntity, context: ItemUsageContext) {
        val linkedFrom = context.world.getBlockEntity(linking)

        if(linkedFrom !is TrafficCabinetBlockEntity) {
            // Traffic cabinet disappeared somehow
            context.player?.sendMessage(Text.literal("The cabinet is no longer there"), true)
            return
        }

        if(!linkedFrom.pos.isWithinDistance(block.pos, MAX_DEVICE_DISTANCE)) {
            // Device is too far!
            context.player?.sendMessage(Text.literal("This device is too far! Max distance is $MAX_DEVICE_DISTANCE blocks"), true)
            return
        }

        if(block is Linkable) {
            val id = block.link(linkedFrom)

            if(id != null) {
                // Device connected!
                context.player?.sendMessage(Text.literal("Device successfully connected with ID $id"), true)
            } else {
                // Something funky happened to the device
                context.player?.sendMessage(Text.literal("Could not link device"), true)
            }
        } else {
            context.player?.sendMessage(Text.literal("Cabinet must be connected to a device"), true)
            return
        }
    }

    private fun linkDevice(device: Linkable, context: ItemUsageContext): ActionResult {
        fun startLink() {
            linking = context.blockPos
            linkingWith = Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY
            context.player?.sendMessage(Text.literal("Right-click a traffic cabinet to link this device"), true)
        }

        if (device.isLinked()) {
            val cabinet = getCabinet(device, context)
            if(cabinet != null) {
                alreadyLinked(device, cabinet, context)
            } else {
                startLink()
            }
        } else {
            startLink()
        }

        return ActionResult.SUCCESS
    }

    private fun completeDeviceToCabinetLink(cabinet: BlockEntity, context: ItemUsageContext) {
        if(cabinet !is TrafficCabinetBlockEntity) {
            // Player right-clicked on something other than a cabinet
            context.player?.sendMessage(Text.literal("Device must be connected to a traffic cabinet"), true)
            return
        }

        if(cabinet.getTotalDevices() >= MAX_DEVICES) {
            // Too many devices connected to this box!
            context.player?.sendMessage(Text.literal("There are too many devices connected to this box! Max is $MAX_DEVICES"), true)
            return
        }

        val linkedFrom = context.world.getBlockEntity(linking)

        if(linkedFrom !is Linkable) {
            // Traffic device disappeared somehow
            context.player?.sendMessage(Text.literal("The device is no longer there"), true)
            return
        }

        if(!linkedFrom.pos.isWithinDistance(cabinet.pos, MAX_DEVICE_DISTANCE)) {
            // Device is too far!
            context.player?.sendMessage(Text.literal("This device is too far! Max distance is $MAX_DEVICE_DISTANCE blocks"), true)
            return
        }

        val id = linkedFrom.link(cabinet)
        if(id != null) {
            // Device connected!
            context.player?.sendMessage(Text.literal("Device successfully connected with ID $id"), true)
        } else {
            // Something funky happened to the device
            context.player?.sendMessage(Text.literal("Could not link device"), true)
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
                if (blockEntity is Linkable) {
                    return linkDevice(blockEntity, context)
                } else if(blockEntity is TrafficCabinetBlockEntity) {
                    return linkCabinet(blockEntity, context)
                } else {
                    context.player?.sendMessage(Text.literal("Right-click a device or traffic cabinet"), true)
                }
            } else if(blockEntity != null) {
                if(linkingWith == Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY) {
                    completeDeviceToCabinetLink(blockEntity, context)
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