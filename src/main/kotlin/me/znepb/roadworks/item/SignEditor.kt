package me.znepb.roadworks.item

import me.znepb.roadworks.RoadworksMain.NAMESPACE
import me.znepb.roadworks.block.sign.CustomSignBlockEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.text.Text
import net.minecraft.util.ActionResult

class SignEditor(settings: Settings) : Item(settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        return if(context.world.getBlockEntity(context.blockPos) is CustomSignBlockEntity) {
            context.player?.openHandledScreen(SimpleNamedScreenHandlerFactory({ syncId, inventory, _ ->
                val screenHandler = SignEditorScreenHandler(syncId, inventory)
                screenHandler.setBlockPosition(context.blockPos)
                screenHandler
            }, Text.translatable("gui.${NAMESPACE}.sign_editor.name")))
            ActionResult.SUCCESS
        } else {
            ActionResult.FAIL
        }
    }
}