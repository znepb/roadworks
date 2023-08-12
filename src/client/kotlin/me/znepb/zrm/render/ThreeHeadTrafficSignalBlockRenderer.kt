package me.znepb.zrm.render

import me.znepb.zrm.Main.ModId
import me.znepb.zrm.block.entity.PostMountableBlockEntity.Companion.getThickest
import me.znepb.zrm.block.signals.SignalLight
import me.znepb.zrm.block.signals.ThreeHeadTrafficSignalBlockEntity
import me.znepb.zrm.render.SignalRenderer.Companion.renderSignal
import me.znepb.zrm.util.PostThickness
import me.znepb.zrm.util.RenderUtils
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import org.joml.Quaternionf

class ThreeHeadTrafficSignalBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    AbstractPostMountableRenderer<ThreeHeadTrafficSignalBlockEntity>() {

    companion object {
        val SIGNAL_FRAME_3 = ModId("block/signal_frame_3")
        val SIGNAL = ModId("block/signal")
    }

    private fun getRotation(direction: Direction): Float {
        return Math.toRadians(when(direction) {
            Direction.NORTH -> 180.0
            Direction.WEST -> 90.0
            Direction.SOUTH -> 0.0
            Direction.EAST -> 270.0
            else -> 0.0
        }).toFloat()
    }

    private fun rotateForSignalRender(matrices: MatrixStack, direction: Direction) {
        matrices.multiply(
            Quaternionf().rotateXYZ(
                Math.toRadians(180.0).toFloat(),
                getRotation(direction),
                0f
            ),
            0.5F, 0.5F, 0.5F
        )
    }

    override fun renderAttachment(
        entity: ThreeHeadTrafficSignalBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val direction = Direction.byId(entity.facing)
        val buffer: VertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull())
        val thickest = getThickest(entity)
        val translateBy = if(entity.wall) {
            8.0/16
        } else {
            -(when (thickest) {
                PostThickness.NONE -> 0
                PostThickness.THIN -> 1
                PostThickness.MEDIUM -> 2
                PostThickness.THICK -> 3
            }).toDouble() / 16
        }.toDouble()

        matrices.push()
        rotateForSignalRender(matrices, direction)
        matrices.translate(0.0, 0.0, translateBy)
        RenderUtils.renderModel(matrices, buffer, light, overlay, SIGNAL_FRAME_3, null)
        matrices.pop()

        matrices.push()
        rotateForSignalRender(matrices, direction)
        matrices.translate(0.0, 0.25, translateBy)
        renderSignal( if(entity.getSignal(SignalLight.GREEN)) "green_on" else "green_off", matrices, vertexConsumers, light, overlay)
        matrices.pop()

        matrices.push()
        rotateForSignalRender(matrices, direction)
        matrices.translate(0.0, 0.0, translateBy)
        renderSignal(if(entity.getSignal(SignalLight.YELLOW)) "yellow_on" else "yellow_off", matrices, vertexConsumers, light, overlay)
        matrices.pop()

        matrices.push()
        rotateForSignalRender(matrices, direction)
        matrices.translate(0.0, -0.25, translateBy)
        renderSignal(if(entity.getSignal(SignalLight.RED)) "red_on" else "red_off", matrices, vertexConsumers, light, overlay)
        matrices.pop()
    }
}