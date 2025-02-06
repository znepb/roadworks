package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.attachment.AttachmentPosition
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.sign.SignAttachment
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.joml.Vector3d

class SignAttachmentRenderer : AttachmentRenderer<SignAttachment> {
    private fun getSignFrontTexture(attachment: SignAttachment): Identifier {
        val tex = attachment.getSignData()?.frontTexture ?: return Identifier("missing")
        return Identifier(tex.namespace, "textures/" + tex.path + ".png")
    }

    private fun getSignBackTexture(attachment: SignAttachment): Identifier {
        val tex = attachment.getSignData()?.backTexture ?: return Identifier("missing")
        return Identifier(tex.namespace, "textures/" + tex.path + ".png")
    }

    private fun getRotation(attachment: SignAttachment) = attachment.getSignData()?.rotation ?: 0

    override fun render(
        attachment: SignAttachment,
        blockEntity: AttachmentContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        offset: Vector3d
    ) {
        val frontTexture = getSignFrontTexture(attachment)
        val backTexture = getSignBackTexture(attachment)
        val rotation = getRotation(attachment)
        val signHeight = attachment.getSignData()?.height ?: 64

        val offsetPos = when(attachment.position) {
            AttachmentPosition.TOP -> 0.0
            AttachmentPosition.MIDDLE -> -0.5 + signHeight.toDouble() / 128.0
            AttachmentPosition.BOTTOM -> -1.0 + signHeight.toDouble() / 64.0
        }

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing, rotation)
        matrices.translate(offset.x, offset.y + offsetPos, offset.z)

        // Render sign front
        val frontBuffer: VertexConsumer = vertexConsumers.getBuffer(RenderLayers.getRenderLayer(frontTexture))
        val frontMatrix = matrices.peek().positionMatrix
        RenderUtils.drawSquare(
            0F, 0F, 0.501F, 0F, 0F, 64F, 64F,
            64, 64, 64F, 64F,
            64, 64, frontBuffer, frontMatrix, light, overlay
        )
        matrices.pop()

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing.opposite, rotation)
        matrices.translate(offset.x, offset.y + offsetPos, -offset.z)

        // Render sign back
        val backBuffer: VertexConsumer = vertexConsumers.getBuffer(RenderLayers.getRenderLayer(backTexture))
        val backMatrix = matrices.peek().positionMatrix
        RenderUtils.drawSquare(
            0F, 0F, 0.501F, 0F, 0F, 64F, 64F,
            64, 64, 64F, 64F,
            64, 64, backBuffer, backMatrix, light, overlay
        )
        matrices.pop()
    }
}