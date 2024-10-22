package me.znepb.roadworks.item

import me.znepb.roadworks.block.sign.custom.CustomSignBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class SignEditor(settings: Settings) : Item(settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        return if(context.world.getBlockEntity(context.blockPos) is CustomSignBlockEntity) {
            context.player?.openHandledScreen(SimpleNamedScreenHandlerFactory({ syncId, inventory, _ ->
                val screenHandler = SignEditorScreenHandler(syncId, inventory)
                screenHandler.setBlockPosition(context.blockPos)
                screenHandler
            }, Text.literal("Sign Editor")))
            ActionResult.SUCCESS
        } else {
            ActionResult.FAIL
        }
    }
}