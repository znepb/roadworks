package me.znepb.roadworks.gui

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.item.AbstractSignEditorScreenHandler
import me.znepb.roadworks.network.EditRouteShieldPacket
import me.znepb.roadworks.network.EditRouteShieldPacketClient.Companion.sendUpdateRouteShieldPacket
import me.znepb.roadworks.network.EditSignPacket
import me.znepb.roadworks.network.EditSignPacketClient.Companion.sendUpdateSignPacket
import me.znepb.roadworks.render.attachments.RenderLayers
import me.znepb.roadworks.render.attachments.RouteShieldAttachmentRenderer
import me.znepb.roadworks.sign.RoadSignAttachment
import me.znepb.roadworks.sign.RouteShieldAttachment
import me.znepb.roadworks.util.Charset
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.InputUtil.GLFW_KEY_0
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import org.lwjgl.glfw.GLFW
import kotlin.math.floor

class RouteShieldEditorScreen(handler: AbstractSignEditorScreenHandler.RouteShieldEditorScreenHandler, playerInventory: PlayerInventory, title: Text) :
    HandledScreen<AbstractSignEditorScreenHandler.RouteShieldEditorScreenHandler>(handler, playerInventory, title) {

    private val background = ModId("textures/gui/route_shield_editor.png")
    private lateinit var completedButton: ButtonWidget
    private var hasSetName = false
    private var currentNumber = ""

    private fun complete() {
        val content = currentNumber
        val pos = this.handler.getBlockPosition()
        val uuid = this.handler.getAttachmentUUID()
        if(pos == null || uuid == null) return
        sendUpdateRouteShieldPacket( EditRouteShieldPacket(pos, uuid, content.toIntOrNull() ?: 0) )

        this.client?.player?.closeHandledScreen()
    }

    override fun init() {
        this.backgroundHeight = 115

        super.init()

        this.completedButton = ButtonWidget.builder(Text.translatable("gui.${RoadworksMain.NAMESPACE}.sign_editor.set")) { this.complete() }
            .dimensions(this.x + 6, this.y + 88, this.backgroundWidth - 12, 20)
            .build()

        this.addSelectableChild(this.completedButton)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            client!!.player!!.closeHandledScreen()
        } else if(keyCode == GLFW.GLFW_KEY_ENTER) {
            complete()
        }

        val num = keyCode - GLFW_KEY_0
        if(num in 0..9 && currentNumber.length < 3) {
            currentNumber += num.toString()
        } else if(keyCode == GLFW.GLFW_KEY_BACKSPACE && currentNumber.isNotEmpty()) {
            currentNumber = currentNumber.substring(0, currentNumber.length - 1)
        }

        return true
    }

    private fun onChanged(str: String) {}

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        client?.textureManager?.bindTexture(background)
        context.drawTexture(background, x, y, 0, 0, backgroundWidth, backgroundHeight)

        if(currentNumber != "") {
            val number = currentNumber.toInt()
            val charWidth = if(number < 100) 15 else 10
            val charHeight = if(number < 100) 21 else 14
            val pixelSpacing = if(number < 100) 3 else 2
            val numberWidth = currentNumber.length * charWidth + (currentNumber.length - 1) * (pixelSpacing * 2)

            var x = this.x + 90 - (numberWidth.toDouble() / 2.0)

            currentNumber.forEach {
                val asNum = it.digitToInt()

                context.drawTexture(
                    RouteShieldAttachmentRenderer.NUMBERS_TEXTURE,
                    x.toInt(),
                    y + 51 - (charHeight / 2),
                    charWidth,
                    charHeight,
                    (asNum * (charWidth + pixelSpacing * 2)).toFloat(),
                    if (number < 100) 14F else 0F,
                    charWidth,
                    charHeight,
                    256,
                    256
                )

                x += charWidth + pixelSpacing
            }
        }

        this.completedButton.render(context, mouseX, mouseY, delta)
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false)
    }

    override fun render(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if(!hasSetName && this.screenHandler.getBlockPosition() != BlockPos.ORIGIN) {
            val be = MinecraftClient.getInstance().player?.world?.getBlockEntity(this.handler.getBlockPosition())

            if (be != null && be is AttachmentContainerBlockEntity) {
                val attachment = this.handler.getAttachmentUUID()?.let { be.getAttachment(it) }
                if(attachment != null && attachment is RouteShieldAttachment) {
                    this.currentNumber = attachment.number.toString()
                }
            }
            hasSetName = true
        }

        super.render(drawContext, mouseX, mouseY, delta)
    }
}