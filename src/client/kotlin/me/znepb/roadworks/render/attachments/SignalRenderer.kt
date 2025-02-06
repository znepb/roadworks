package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.datagen.ModelProvider
import me.znepb.roadworks.signal.AbstractSignalAttachment
import me.znepb.roadworks.signal.SignalLight
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

class SignalRenderer(
    private val attachment: AbstractSignalAttachment,
    private val matrices: MatrixStack,
    private val vertexConsumer: VertexConsumerProvider,
    private val light: Int,
    private val overlay: Int
) {
    companion object {
        val SIGNAL_MODEL_IDS = ModelProvider.signals.map { RoadworksMain.ModId("block/signal_$it") }
    }

    val buffer: VertexConsumer = vertexConsumer.getBuffer(TexturedRenderLayers.getEntityTranslucentCull())

    fun renderSignal(
        signalLight: SignalLight,
        x: Double,
        y: Double,
        offset: Vector3d
    ) {
        val modelLocation =
            RoadworksMain.ModId("block/signal_${signalLight.light}_${if(attachment.isSignalActive(signalLight)) "on" else "off"}")

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing.opposite, 0)
        matrices.translate(offset.x + x, offset.y + y, -offset.z)

        val model = MinecraftClient.getInstance().bakedModelManager.getModel(modelLocation)

        if(model != null) {
            val random = Random.create(0)

            random.setSeed(42L)

            val matrix = matrices.peek()
            val quads: Iterator<*> = model.getQuads(null as BlockState?, null, random).iterator()
            while (quads.hasNext()) {
                val bakedquad = quads.next() as BakedQuad
                val face = bakedquad.face

                val sprite = bakedquad.sprite

                val newLight =
                    if (!sprite.contents.id.path.endsWith("signal_back") && attachment.isSignalActive(signalLight) && face == Direction.NORTH)
                        LightmapTextureManager.MAX_LIGHT_COORDINATE
                    else
                        light

                buffer.quad(matrix, bakedquad, 1F, 1F, 1F, newLight, overlay)
            }
        } else {
            RenderUtils.renderModel(
                matrices,
                buffer,
                light,
                overlay,
                MinecraftClient.getInstance().bakedModelManager.missingModel,
                this.attachment.facing,
                Direction.entries
            )
        }

        matrices.pop()
    }
}