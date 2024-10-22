package me.znepb.roadworks.gui

import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.RoadworksMain.NAMESPACE
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.block.sign.CustomSignBlockEntity
import me.znepb.roadworks.item.SignEditorScreenHandler
import me.znepb.roadworks.network.EditSignPacket
import me.znepb.roadworks.network.EditSignPacketClient.Companion.sendUpdateSignPacket
import me.znepb.roadworks.util.Charset
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import org.lwjgl.glfw.GLFW
import kotlin.math.floor

class SignEditorScreen(handler: SignEditorScreenHandler, playerInventory: PlayerInventory, title: Text) :
    HandledScreen<SignEditorScreenHandler>(handler, playerInventory, title) {

    private val background = ModId("textures/gui/sign_editor.png")
    private val charsetTex = ModId("textures/block/signs/charset.png")
    private val signTexture = ModId("textures/block/signs/background_green.png")
    private lateinit var nameField: TextFieldWidget
    private val customButtons: MutableMap<Charset, ButtonWidget> = mutableMapOf()
    private lateinit var completedButton: ButtonWidget
    private var hasSetName = false

    private fun renderCharsetCharacter(context: DrawContext, char: Charset, x: Int, y: Int) {
        context.drawTexture(
            charsetTex, x, y, 0,
            char.x * 8F, char.y * 8F,
            char.w, 8, Charset.CHARSET_WIDTH, Charset.CHARSET_HEIGHT)
    }

    private fun complete() {
        val content = Charset.fromLongString(this.nameField.text)
        logger.info(this.handler.getBlockPosition().toString())
        sendUpdateSignPacket( EditSignPacket(this.handler.getBlockPosition(), content) )

        this.client?.player?.closeHandledScreen()
    }

    override fun init() {
        this.backgroundHeight = 176

        super.init()

        this.nameField = TextFieldWidget(this.textRenderer, this.x + 6, this.y + 60, 164, 12, Text.literal("Sign Text"))
        this.nameField.setMaxLength(256)
        this.nameField.text = ""
        this.nameField.setChangedListener(this::onChanged)
        this.addSelectableChild(this.nameField)
        this.setInitialFocus(this.nameField)

        val buttonY =
            this.nameField.y + this.nameField.height + ((floor((customCharactersToShow.size).toDouble() / 7.0) + 1) * 24).toInt() + 6

        this.completedButton = ButtonWidget.builder(Text.translatable("gui.${NAMESPACE}.sign_editor.set")) { this.complete() }
            .dimensions(this.x + 6, buttonY, this.backgroundWidth - 12, 20)
            .build()

        this.addSelectableChild(this.completedButton)

        customCharactersToShow.forEachIndexed { index, charset ->
            val x = (index % 7) * 24
            val y = (floor(index.toDouble() / 7.0) * 24).toInt()
            val button = ButtonWidget.builder(Text.literal("")) {
                this.nameField.write(charset.char)
            }
                .dimensions(this.x + x + 6, this.nameField.y + this.nameField.height + y + 6, 20, 20)
                .build()

            this.addSelectableChild(button)
            this.customButtons[charset] = button
        }
    }

    private fun drawSignPreview(context: DrawContext) {
        val center = this.x + (this.backgroundWidth) / 2
        val contents = Charset.fromLongString(this.nameField.text)
        var size = if(contents.isEmpty()) 8 else -1
        contents.forEach {
            size += (it.w + 1)
        }

        val left = center - (size + 8) / 2
        val y = this.y + 28

        context.drawTexture(this.signTexture, left, y, 4, 4, 0F, 0F, 4, 4, 8, 8)
        context.drawTexture(this.signTexture, left + size + 4, y, 4, 4, 4F, 0F, 4, 4, 8, 8)
        context.drawTexture(this.signTexture, left, y + 12, 4, 4, 0F, 4F, 4, 4, 8, 8)
        context.drawTexture(this.signTexture, left + size + 4, y + 12, 4, 4, 4F, 4F, 4, 4, 8, 8)

        // L/R
        context.drawTexture(this.signTexture, left, y + 4 , 4, 8, 0F, 3F, 4, 2, 8, 8)
        context.drawTexture(this.signTexture, left + size + 4, y + 4, 4, 8, 4F, 3F, 4, 2, 8, 8)

        // T/B
        context.drawTexture(this.signTexture, left + 4, y, size, 4, 3F, 0F, 2, 4, 8, 8)
        context.drawTexture(this.signTexture, left + 4, y + 12, size, 4, 3F, 4F, 2, 4, 8, 8)

        // C
        context.drawTexture(this.signTexture, left + 4, y + 4, size, 8, 3F, 3F, 2, 2, 8, 8)

        // Text
        var pos = left
        contents.forEach {
            context.drawTexture(this.charsetTex, 4 + pos, y + 4, it.w, 8, it.x * 8F, it.y * 8F, it.w, 8, Charset.CHARSET_WIDTH, Charset.CHARSET_HEIGHT)
            pos += it.w + 1
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            client!!.player!!.closeHandledScreen()
        } else if(keyCode == GLFW.GLFW_KEY_ENTER) {
            complete()
        }

        return if (!nameField.keyPressed(keyCode, scanCode, modifiers) && !nameField.isActive) super.keyPressed(
            keyCode,
            scanCode,
            modifiers
        ) else true
    }

    private fun onChanged(str: String) {}

    override fun handledScreenTick() {
        this.nameField.tick()
        super.handledScreenTick()
    }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        client?.textureManager?.bindTexture(background)
        context.drawTexture(background, x, y, 0, 0, backgroundWidth, backgroundHeight)
        drawSignPreview(context)
        this.customButtons.forEach {
            it.value.render(context, mouseX, mouseY, delta)
            renderCharsetCharacter(context, it.key, it.value.x + 10 - (it.key.w / 2), it.value.y + 6)
        }
        this.completedButton.render(context, mouseX, mouseY, delta)
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false)
    }

    override fun render(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if(!hasSetName && this.screenHandler.getBlockPosition() != BlockPos.ORIGIN) {
            val be = MinecraftClient.getInstance().player?.world?.getBlockEntity(this.handler.getBlockPosition())

            if (be != null && be is CustomSignBlockEntity) {
                var text = ""
                be.contents.forEach {
                    text += it.toString()
                }
                this.nameField.text = text
            }

            hasSetName = true
        }

        super.render(drawContext, mouseX, mouseY, delta)
        this.nameField.render(drawContext, mouseX, mouseY, delta)
    }

    companion object {
        val customCharactersToShow = listOf(
            Charset.ROAD,
            Charset.STREET,
            Charset.AVE,
            Charset.BLVD,
            Charset.WAY,
            Charset.DR,
            Charset.PK,
            Charset.HWY,
            Charset.ARROW_RIGHT,
            Charset.ARROW_LEFT,
            Charset.ARROW_UP,
            Charset.ARROW_DOWN,
            Charset.FORBIDDEN,
            Charset.WARNING,
            Charset.ND,
            Charset.TH,
            Charset.NORTH,
            Charset.EAST,
            Charset.SOUTH,
            Charset.WEST
        )
    }
}