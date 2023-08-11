package me.znepb.zrm.render

import me.znepb.zrm.Main.ModId
import me.znepb.zrm.datagen.ModelProvider
import me.znepb.zrm.util.RenderUtils.Companion.renderModel
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack

class SignalRenderer {
    companion object {
        val SIGNAL_MODEL_IDS = ModelProvider.signals.map { ModId("block/signal_$it") }

        fun renderSignal(name: String, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
            val buffer: VertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull())
            renderModel(matrices, buffer, light, overlay, ModId("block/signal_$name"), null)
        }
    }
}