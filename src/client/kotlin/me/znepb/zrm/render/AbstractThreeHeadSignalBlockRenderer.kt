package me.znepb.zrm.render

import me.znepb.zrm.Main.ModId
import me.znepb.zrm.block.post.AbstractPostMountableBlockEntity.Companion.getThickest
import me.znepb.zrm.block.signals.AbstractTrafficSignalBlockEntity
import me.znepb.zrm.block.signals.SignalLight
import me.znepb.zrm.util.PostThickness
import me.znepb.zrm.util.RenderUtils
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction

abstract class AbstractThreeHeadSignalBlockRenderer(
    private val ctx: BlockEntityRendererFactory.Context,
    private val redSignalLight: SignalLight,
    private val yellowSignalLight: SignalLight,
    private val greenSignalLight: SignalLight
) :
    AbstractPostMountableRenderer<AbstractTrafficSignalBlockEntity>() {

    companion object {
        val SIGNAL_FRAME_3 = ModId("block/signal_frame_3")
        val SIGNAL = ModId("block/signal")
    }

    override fun renderAttachment(
        entity: AbstractTrafficSignalBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {

        val direction = Direction.byId(entity.facing)
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

        val renderer = SignalRenderer(entity, matrices, vertexConsumers, light, overlay, direction, translateBy)

        matrices.push()
        renderer.rotateForSignalRender()
        matrices.translate(0.0, 0.0, translateBy)
        RenderUtils.renderModel(matrices, renderer.buffer, light, overlay, SIGNAL_FRAME_3, null)
        matrices.pop()

        renderer.renderSignal(redSignalLight, 0.0, 0.25)
        renderer.renderSignal(yellowSignalLight, 0.0, 0.0)
        renderer.renderSignal(greenSignalLight, 0.0, -0.25)
    }
}