package me.znepb.roadworks.render

import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import me.znepb.roadworks.block.signals.AbstractTrafficSignalBlockEntity
import me.znepb.roadworks.block.signals.SignalLight
import me.znepb.roadworks.datagen.ModelProvider
import me.znepb.roadworks.util.RenderUtils
import me.znepb.roadworks.util.RenderUtils.renderModel
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
import org.joml.Quaternionf

class PostMountRenderer(
    private val entity: AbstractPostMountableBlockEntity,
    private val matrices: MatrixStack,
    private val vertexConsumer: VertexConsumerProvider,
    private val light: Int,
    private val overlay: Int,
    private val direction: Direction,
    private val postOffset: Double
) {
    companion object {
        val SIGNAL_MODEL_IDS = ModelProvider.signals.map { ModId("block/signal_$it") }
    }

    val buffer: VertexConsumer = vertexConsumer.getBuffer(TexturedRenderLayers.getEntityTranslucentCull())

    fun rotateForRender() {
        matrices.multiply(
            Quaternionf().rotateXYZ(
                Math.toRadians(180.0).toFloat(),
                RenderUtils.getRotationFromDirection(direction),
                Math.toRadians(180.0).toFloat()
            ),
            0.5F, 0.5F, 0.5F
        )
    }

    fun renderSignal(
        signalLight: SignalLight,
        x: Double,
        y: Double
    ) {
        if(entity !is AbstractTrafficSignalBlockEntity) return

        val modelLocation =
            ModId(if (entity.getSignal(signalLight))
                "block/signal_${signalLight.light}_on"
            else
                "block/signal_${signalLight.light}_off")

        matrices.push()
        rotateForRender()
        matrices.translate(x, y, postOffset)

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
                    if (!sprite.contents.id.path.endsWith("signal_back") && entity.getSignal(signalLight) && face == Direction.NORTH)
                        LightmapTextureManager.MAX_LIGHT_COORDINATE
                    else
                        light
                
                buffer.quad(matrix, bakedquad, 1F, 1F, 1F, newLight, overlay)
            }
        } else {
            renderModel(matrices, buffer, light, overlay, MinecraftClient.getInstance().bakedModelManager.missingModel, direction)
        }

        matrices.pop()
    }
}