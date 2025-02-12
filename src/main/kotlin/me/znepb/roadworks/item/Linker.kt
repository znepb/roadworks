package me.znepb.roadworks.item

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.attachment.LinkableAttachment
import me.znepb.roadworks.cabinet.TrafficCabinetBlockEntity
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import net.minecraft.block.entity.BlockEntity
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
import java.util.*

class Linker(settings: Settings) : Item(settings) {
    var linking: BlockPos? = null
    var linkingWith: LinkingFrom? = null
    var linkingWithUUID: UUID? = null

    enum class LinkingFrom {
        CABINET, DEVICE
    }

    private fun getCabinet(blockEntity: LinkableAttachment, context: ItemUsageContext): TrafficCabinetBlockEntity? {
        val be = context.world?.getBlockEntity(blockEntity.linkPosition)
        return if(be is TrafficCabinetBlockEntity) { be } else null
    }

    private fun unlink(
        device: LinkableAttachment,
        cabinet: TrafficCabinetBlockEntity,
        context: ItemUsageContext)
    {
        device.unlink()
        cabinet.removeConnection(context.blockPos, device.id)
        context.player?.sendMessage(Text.literal("Device unlinked"), true)
    }

    private fun alreadyLinked(
        device: LinkableAttachment,
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
                            "${cabinet.getConnectionIdentifierFromBlockPosAndUUID(context.blockPos, device.id)}. " +
                            "Crouch-Right click to unlink."
                ),
                true
            )
        }
    }

    // Start linking from the cabinet
    private fun linkCabinet(cabinet: TrafficCabinetBlockEntity, context: ItemUsageContext): ActionResult {
        if(cabinet.getTotalDevices() > RoadworksMain.CONFIG!!.trafficCabinet.maxDevices) {
            // Too many devices connected to this box!
            context.player?.sendMessage(Text.literal("There are too many devices connected to this box! Max is ${RoadworksMain.CONFIG!!.trafficCabinet.maxDevices}"), true)
            return ActionResult.SUCCESS
        }

        linking = context.blockPos
        linkingWith = LinkingFrom.CABINET
        context.player?.sendMessage(Text.literal("Right-click a traffic device to link to this cabinet"), true)

        return ActionResult.SUCCESS
    }

    private fun completeLinkToCabinet(attachment: LinkableAttachment, context: ItemUsageContext) {
        val linkedFrom = context.world.getBlockEntity(linking)

        if(linkedFrom !is TrafficCabinetBlockEntity) {
            // Traffic cabinet disappeared somehow
            context.player?.sendMessage(Text.literal("The cabinet is no longer there"), true)
            return
        }

        if(!linkedFrom.pos.isWithinDistance(attachment.container.pos, RoadworksMain.CONFIG!!.trafficCabinet.maxLinkDistance)) {
            // Device is too far!
            context.player?.sendMessage(Text.literal("This device is too far! Max distance is ${RoadworksMain.CONFIG!!.trafficCabinet.maxLinkDistance} blocks"), true)
            return
        }

        val id = attachment.link(linkedFrom)

        if(id != null) {
            // Device connected!
            context.player?.sendMessage(Text.literal("Device successfully connected with ID $id"), true)
        } else {
            // Something funky happened to the device
            context.player?.sendMessage(Text.literal("Could not link device"), true)
        }
    }

    // Start linking from the device
    private fun linkDevice(device: LinkableAttachment, context: ItemUsageContext): ActionResult {
        fun startLink() {
            linking = device.container.pos
            linkingWithUUID = device.id
            linkingWith = LinkingFrom.DEVICE
            context.player?.sendMessage(Text.literal("Right-click a traffic cabinet to link this device"), true)
        }

        if (device.linked) {
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
        if(linkingWithUUID == null) {
            // Somehow linkingWithUUID was null
            context.player?.sendMessage(Text.literal("Something went terribly wrong. Try again?"), true)
            return
        }

        if(cabinet !is TrafficCabinetBlockEntity) {
            // Player right-clicked on something other than a cabinet
            context.player?.sendMessage(Text.literal("Device must be connected to a traffic cabinet"), true)
            return
        }

        if(cabinet.getTotalDevices() >= RoadworksMain.CONFIG!!.trafficCabinet.maxDevices) {
            // Too many devices connected to this box!
            context.player?.sendMessage(Text.literal("There are too many devices connected to this box! Max is ${RoadworksMain.CONFIG!!.trafficCabinet.maxDevices}"), true)
            return
        }

        val linkedFromBlockEntity = context.world.getBlockEntity(linking)

        if(linkedFromBlockEntity !is AttachmentContainerBlockEntity) {
            // Traffic device disappeared somehow
            context.player?.sendMessage(Text.literal("The device is no longer there"), true)
            return
        }

        val linkedFrom = linkedFromBlockEntity.getAttachment(linkingWithUUID!!)

        if(linkedFrom !is LinkableAttachment) {
            // Traffic device disappeared somehow
            context.player?.sendMessage(Text.literal("The device is no longer there"), true)
            return
        }

        if(!linkedFrom.container.pos.isWithinDistance(cabinet.pos, RoadworksMain.CONFIG!!.trafficCabinet.maxLinkDistance)) {
            // Device is too far!
            context.player?.sendMessage(Text.literal("This device is too far! Max distance is ${RoadworksMain.CONFIG!!.trafficCabinet.maxLinkDistance} blocks"), true)
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
                if (blockEntity is AttachmentContainerBlockEntity) {
                    val attachment = context.player?.let { blockEntity.getPlayerAttachmentLookingAt(it) }
                    return if(attachment != null && attachment is LinkableAttachment) linkDevice(attachment, context) else ActionResult.CONSUME
                } else if(blockEntity is TrafficCabinetBlockEntity) {
                    return linkCabinet(blockEntity, context)
                } else {
                    context.player?.sendMessage(Text.literal("Right-click a device or traffic cabinet"), true)
                }
            } else if(blockEntity != null) {
                if(linkingWith == LinkingFrom.DEVICE) {
                    completeDeviceToCabinetLink(blockEntity, context)
                    reset()
                } else if(linkingWith == LinkingFrom.CABINET && blockEntity is AttachmentContainerBlockEntity) {
                    val attachment = context.player?.let { blockEntity.getPlayerAttachmentLookingAt(it) }
                    if(attachment != null && attachment is LinkableAttachment) completeLinkToCabinet(attachment, context)
                    reset()
                }
            }

            return ActionResult.SUCCESS
        }
    }
}