package me.znepb.roadworks.item

import me.znepb.roadworks.RoadworksMain.NAMESPACE
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.item.AbstractSignEditorScreenHandler.Companion.sendDataToClient
import me.znepb.roadworks.sign.RoadSignAttachment
import me.znepb.roadworks.sign.RouteShieldAttachment
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.text.Text
import net.minecraft.util.ActionResult

class SignEditor(settings: Settings) : Item(settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if(context.world.isClient) return ActionResult.CONSUME
        val be = context.world.getBlockEntity(context.blockPos)
        return if(be != null && be is PostContainerBlockEntity) {
            val attachment = context.player?.let { be.getPlayerAttachmentLookingAt(it) }
            return if(attachment != null && attachment is RoadSignAttachment) {
                context.player?.openHandledScreen(SimpleNamedScreenHandlerFactory({ syncId, inventory, _ ->
                    val screenHandler = AbstractSignEditorScreenHandler.RoadSignEditorScreenHandler(syncId, inventory)
                    screenHandler
                }, Text.translatable("gui.${NAMESPACE}.sign_editor.name")))
                val player = context.player?.server?.playerManager?.getPlayer(context.player?.uuid)
                if (player != null) {
                    sendDataToClient(player, AbstractSignEditorScreenHandler.SyncData(context.blockPos, attachment.id))
                }
                ActionResult.SUCCESS
            } else if(attachment != null && attachment is RouteShieldAttachment) {
                context.player?.openHandledScreen(SimpleNamedScreenHandlerFactory({ syncId, inventory, _ ->
                    val screenHandler = AbstractSignEditorScreenHandler.RouteShieldEditorScreenHandler(syncId, inventory)
                    screenHandler
                }, Text.translatable("gui.${NAMESPACE}.route_shield_editor.name")))
                val player = context.player?.server?.playerManager?.getPlayer(context.player?.uuid)
                if (player != null) {
                    sendDataToClient(player, AbstractSignEditorScreenHandler.SyncData(context.blockPos, attachment.id))
                }
                ActionResult.SUCCESS
            } else { ActionResult.CONSUME }
        } else {
            ActionResult.FAIL
        }
    }
}