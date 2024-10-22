package me.znepb.roadworks.block.sign

import me.znepb.roadworks.RoadworksMain
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class SignBlockItem(block: SignBlock, settings: Settings) : BlockItem(block, settings) {
    override fun getName(stack: ItemStack): Text {
        val name: MutableText = Text.translatable(stack.translationKey)
        val nbt = getBlockEntityNbt(stack)
        val signType = if(nbt?.contains("sign_type") == true) RoadworksMain.SIGN_TYPES[Identifier(nbt.getString("sign_type"))] else null
        return Text.translatable(signType?.name ?: "block.roadworks.sign")
    }
}