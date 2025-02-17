package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.attachment.AttachmentPosition
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.sign.RoadSignAttachment
import me.znepb.roadworks.sign.RouteShieldAttachment
import me.znepb.roadworks.util.Charset
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.joml.Vector3d

class RouteShieldAttachmentRenderer : AttachmentRenderer<RouteShieldAttachment> {
    companion object {
        val FRONT_TEXTURE = RoadworksMain.ModId("textures/block/signs/route_shield.png")
        val BACK_TEXTURE = RoadworksMain.ModId("textures/block/signs/back_large_square.png")
        val NUMBERS_TEXTURE = RoadworksMain.ModId("textures/block/signs/highway_shield_numbers.png")
    }

    override fun render(
        attachment: RouteShieldAttachment,
        blockEntity: AttachmentContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        offset: Vector3d
    ) {
        val number = attachment.number

        // Get size of contents
        val width = if(number < 100) 21.0 else 14.0
        val renderedCharWidth = if(number < 100) 15.0 else 10.0
        val height = if(number < 100) 21.0 else 14.0
        val charOffset = if(number < 10) 1 else if(number < 100) 3 else 2
        val pixelCount = number.toString().length * renderedCharWidth

        val offsetPos = 0.0

        val finalOffset = offset.add(Vector3d(0.0, offsetPos,  0.0078125))

        // Prepare matrices
        matrices.push()
        matrices.translate(0.5F, 0.5F, 0.5F)
        matrices.multiply(attachment.facing.rotationQuaternion.rotateXYZ((Math.PI / 2).toFloat(), Math.PI.toFloat(), Math.PI.toFloat()))
        matrices.translate(-0.5F, -0.5F, -0.5F)
        matrices.translate(finalOffset.x, finalOffset.y, finalOffset.z)

        // Render sign background
        val buffer: VertexConsumer = vertexConsumers.getBuffer(RenderLayers.getRenderLayer(FRONT_TEXTURE))
        val matrix = matrices.peek().positionMatrix
        RenderUtils.drawSquare(
            0F, 0F, 0.501F, 0F, 0F, 64F, 64F,
            64, 64, 64F, 64F,
            64, 64, buffer, matrix, light, overlay
        )

        // Render sign text
        val charsetBuffer: VertexConsumer = vertexConsumers.getBuffer(RenderLayers.getRenderLayer(NUMBERS_TEXTURE))
        var x = 32.0 - (pixelCount / 2) - charOffset
        number.toString().forEach {
            val num = it.digitToIntOrNull()?.toDouble()

            if(num != null)
                RenderUtils.drawSquare(
                    x.toFloat(), 32F - (height / 2).toFloat(), 0.51F, (num * width).toFloat(), if(number < 100) 14F else 0F, width.toFloat(), height.toFloat(),
                    64, 64, width.toFloat(), height.toFloat(),
                    256, 256, charsetBuffer, matrix, light, overlay
                )

            x += (width - charOffset)
        }

        matrices.pop()

        // Render sign back
        matrices.push()

        matrices.translate(0.5F, 0.5F, 0.5F)
        matrices.multiply(attachment.facing.rotationQuaternion.rotateXYZ((Math.PI / 2).toFloat(), 0.0F, Math.PI.toFloat()))
        matrices.translate(-0.5F, -0.5F, -0.5F)
        matrices.translate(finalOffset.x, finalOffset.y, -finalOffset.z)

        val backBuffer: VertexConsumer = vertexConsumers.getBuffer(RenderLayers.getRenderLayer(BACK_TEXTURE))
        val backMatrix = matrices.peek().positionMatrix
        RenderUtils.drawSquare(
            0F, 0F, 0.501F, 0F, 0F, 64F, 64F,
            64, 64, 64F, 64F,
            64, 64, backBuffer, backMatrix, light, overlay
        )

        matrices.pop()
    }
}