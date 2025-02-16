package me.znepb.roadworks.sign

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.attachment.Attachment
import me.znepb.roadworks.attachment.AttachmentItem
import me.znepb.roadworks.attachment.AttachmentType
import net.minecraft.item.BlockItem.getBlockEntityNbt
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class SignAttachmentItem(attachment: AttachmentType<out Attachment>, settings: Settings) : AttachmentItem(attachment, settings) {
    override fun getName(stack: ItemStack): Text {
        val nbt = stack.orCreateNbt
        val signType = if(nbt?.contains("sign_type") == true) RoadworksMain.signageManager.getSign(Identifier(nbt.getString("sign_type"))) else null
        return Text.translatable(signType?.name ?: "block.roadworks.sign")
    }
}