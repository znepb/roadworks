package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.attachment.AttachmentPosition
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.signal.BeaconAttachment
import me.znepb.roadworks.train.TrainBellAttachment
import me.znepb.roadworks.train.TrainSignalAttachment
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import org.joml.Vector3d

class TrainSignalAttachmentRenderer : AttachmentRenderer<TrainSignalAttachment> {
    companion object {
        val BEACON_BACKBEAM = RoadworksMain.ModId("block/train_crossing_backbeam")
        val BEACON_BASE = RoadworksMain.ModId("block/train_crossing_beacon")
        val BEACON_ON = RoadworksMain.ModId("block/train_crossing_beacon_on")
        val BEACON_OFF = RoadworksMain.ModId("block/train_crossing_beacon_off")
    }

    override fun render(
        attachment: TrainSignalAttachment,
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
        matrices.translate(offset.x, offset.y + (2.0 / 16.0), -offset.z + (1.0 / 16.0))
        RenderUtils.renderModel(matrices, buffer, light, overlay, BEACON_BACKBEAM, null)
        matrices.pop()

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing.opposite, 0)
        matrices.translate(offset.x + (-5.0 / 16.0), offset.y, -offset.z)
        RenderUtils.renderModel(matrices, buffer, light, overlay, BEACON_BASE, null)
        matrices.pop()

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing.opposite, 0)
        matrices.translate(offset.x + (4.0 / 16.0), offset.y, -offset.z)
        RenderUtils.renderModel(matrices, buffer, light, overlay, BEACON_BASE, null)
        matrices.pop()

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing.opposite, 0)
        matrices.translate(offset.x + (4.0 / 16.0), offset.y, -offset.z - 0.0078125)
        renderSignal(matrices, buffer, overlay, light, attachment, attachment.isLeftOn())
        matrices.pop()

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing.opposite, 0)
        matrices.translate((-5.0 / 16.0), offset.y, -offset.z - 0.0078125)
        renderSignal(matrices, buffer, overlay, light, attachment, attachment.isRightOn())
        matrices.pop()
    }

    private fun renderSignal(matrices: MatrixStack, buffer: VertexConsumer, overlay: Int, light: Int, attachment: TrainSignalAttachment, on: Boolean) {
        val modelLocation = if(on) BEACON_ON else BEACON_OFF
        val model = MinecraftClient.getInstance().bakedModelManager.getModel(modelLocation)

        if(model != null) {
            val random = Random.create(0)

            random.setSeed(42L)

            val matrix = matrices.peek()
            val quads: Iterator<*> = model.getQuads(null as BlockState?, null, random).iterator()
            while (quads.hasNext()) {
                val bakedquad = quads.next() as BakedQuad
                buffer.quad(matrix, bakedquad, 1F, 1F, 1F, if(on) LightmapTextureManager.MAX_LIGHT_COORDINATE else light, overlay)
            }
        }
    }
}