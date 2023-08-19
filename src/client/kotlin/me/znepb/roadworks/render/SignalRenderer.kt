package me.znepb.roadworks.render

import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.block.signals.AbstractTrafficSignalBlockEntity
import me.znepb.roadworks.block.signals.SignalLight
import me.znepb.roadworks.datagen.ModelProvider
import me.znepb.roadworks.util.RenderUtils
import me.znepb.roadworks.util.RenderUtils.Companion.renderModel
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import org.joml.Quaternionf

class SignalRenderer(
    private val entity: AbstractTrafficSignalBlockEntity,
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

    fun rotateForSignalRender() {
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
        matrices.push()
        rotateForSignalRender()
        matrices.translate(x, y, postOffset)
        renderModel(
            matrices, buffer, light, overlay,
            ModId(
                if (entity.getSignal(signalLight))
                    "block/signal_${signalLight.light}_on"
                else
                    "block/signal_${signalLight.light}_off"
            ),
            null
        )
        matrices.pop()
    }
}