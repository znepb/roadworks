package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.attachment.AttachmentPosition
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.signal.BlankoutAttachment
import me.znepb.roadworks.train.TrainBellAttachment
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.render.LightmapTextureManager.MAX_LIGHT_COORDINATE
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3d

class BlankoutAttachmentRenderer : AttachmentRenderer<BlankoutAttachment> {
    companion object {
        val SQUARE = RoadworksMain.ModId("block/blankout_square")
        val RECTANGLE = RoadworksMain.ModId("block/blankout_rectangle")
    }

    override fun render(
        attachment: BlankoutAttachment,
        blockEntity: AttachmentContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        offset: Vector3d
    ) {
        val buffer: VertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull())

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing.opposite, 0)
        matrices.translate(offset.x, offset.y, -offset.z - 1.0 / 16.0)
        RenderUtils.renderModel(matrices, buffer, light, overlay, if(attachment.isRectangular) RECTANGLE else SQUARE, null)
        matrices.pop()

        if(attachment.isActive()) {
            matrices.push()
            AttachmentRenderer.translateForCenter(matrices, attachment.facing, 0)
            matrices.translate(offset.x, offset.y, offset.z)
            // Render sign front
            val frontBuffer: VertexConsumer =
                vertexConsumers.getBuffer(RenderLayers.getRenderLayer(ModId("textures/block/${attachment.texture.path}.png")))
            val frontMatrix = matrices.peek().positionMatrix
            RenderUtils.drawSquare(
                0F, 0F, (9F / 16F) + 0.01F, 0F, 0F, 64F, 64F,
                64, 64, 64F, 64F,
                64, 64, frontBuffer, frontMatrix, MAX_LIGHT_COORDINATE, overlay
            )
            matrices.pop()
        }
    }
}