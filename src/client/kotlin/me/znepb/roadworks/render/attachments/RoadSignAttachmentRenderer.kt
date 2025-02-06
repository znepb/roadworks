package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.attachment.AttachmentPosition
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.sign.RoadSignAttachment
import me.znepb.roadworks.util.Charset
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.joml.Vector3d

class RoadSignAttachmentRenderer : AttachmentRenderer<RoadSignAttachment> {
    companion object {
        val SIGN_TEX_HEIGHT = 8
        val SIGN_TEX_WIDTH = 8
        val SIGN_CORNER_SIZE = 3F

        val FRONT_TEXTURES = mutableMapOf<String, Identifier>().also {
            it["green"] = RoadworksMain.ModId("textures/block/signs/background_green.png")
            it["yellow"] = RoadworksMain.ModId("textures/block/signs/background_yellow.png")
        }
        val FRONT_COLORS = mutableMapOf<String, Triple<Int, Int, Int>>().also {
            it["green"] = Triple(255, 255, 255)
            it["yellow"] = Triple(0, 0, 0)
        }
        val BACK_TEXTURE = RoadworksMain.ModId("textures/block/signs/back_full.png")
    }

    override fun render(
        attachment: RoadSignAttachment,
        blockEntity: AttachmentContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        offset: Vector3d
    ) {
        val contents = attachment.contents
        val color = attachment.color

        // Get size of contents
        var size = -1
        contents.forEach {
            size += (it.w + 1)
        }

        val pixelCount = size.toFloat()
        val frontTexture = FRONT_TEXTURES[color] ?: Identifier("")
        val frontColor = FRONT_COLORS[color] ?: Triple(255, 255, 255)

        val offsetPos = when(attachment.position) {
            AttachmentPosition.TOP -> 0.0
            AttachmentPosition.MIDDLE -> -0.5 + 16.0 / 128.0
            AttachmentPosition.BOTTOM -> -1.0 + 16.0 / 64.0
        }

        val finalOffset = offset.add(Vector3d(0.0, offsetPos,  0.0078125))

        // Prepare matrices
        matrices.push()
        matrices.translate(0.5F, 0.5F, 0.5F)
        matrices.multiply(attachment.facing.rotationQuaternion.rotateXYZ((Math.PI / 2).toFloat(), Math.PI.toFloat(), Math.PI.toFloat()))
        matrices.translate(-0.5F, -0.5F, -0.5F)
        matrices.translate(finalOffset.x, finalOffset.y, finalOffset.z)

        // Render sign background
        val buffer: VertexConsumer = vertexConsumers.getBuffer(RenderLayers.getRenderLayer(frontTexture!!))
        val matrix = matrices.peek().positionMatrix

        RenderUtils.nineSplice(
            (32F - ((pixelCount + 8) / 2)),
            48F,
            0.5F,
            pixelCount + 8,
            16F,
            SIGN_CORNER_SIZE,
            SIGN_CORNER_SIZE,
            SIGN_CORNER_SIZE,
            SIGN_CORNER_SIZE,
            64,
            64,
            SIGN_TEX_WIDTH,
            SIGN_TEX_HEIGHT,
            buffer,
            matrix,
            light,
            overlay
        )

        // Render sign text
        val charsetBuffer: VertexConsumer = vertexConsumers.getBuffer(RenderLayers.getRenderLayer(Charset.TEXTURE))
        var x = 32F - (pixelCount / 2)
        contents.forEach {
            RenderUtils.drawSquare(
                x, 52F, 0.501F, it.x * 8F, it.y * 8F, it.w.toFloat(), 8F,
                64, 64, it.w.toFloat(), 8F,
                Charset.CHARSET_WIDTH, Charset.CHARSET_HEIGHT, charsetBuffer, matrix, light, overlay,
                frontColor
            )

            x += it.w + 1
        }

        matrices.pop()

        // Render sign back
        matrices.push()

        matrices.translate(0.5F, 0.5F, 0.5F)
        matrices.multiply(attachment.facing.rotationQuaternion.rotateXYZ((Math.PI / 2).toFloat(), 0.0F, Math.PI.toFloat()))
        matrices.translate(-0.5F, -0.5F, -0.5F)
        matrices.translate(finalOffset.x, finalOffset.y, -finalOffset.z)

        val backBuffer: VertexConsumer = vertexConsumers.getBuffer(RenderLayers.getRenderLayer(BACK_TEXTURE))
        RenderUtils.nineSplice(
            (32F - ((pixelCount + 8) / 2)),
            48F,
            0.5F,
            pixelCount + 8,
            16F,
            SIGN_CORNER_SIZE,
            SIGN_CORNER_SIZE,
            SIGN_CORNER_SIZE,
            SIGN_CORNER_SIZE,
            64,
            64,
            64,
            64,
            backBuffer,
            matrices.peek().positionMatrix,
            light,
            overlay
        )

        matrices.pop()
    }
}