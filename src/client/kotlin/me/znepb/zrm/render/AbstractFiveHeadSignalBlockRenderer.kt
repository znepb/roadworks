package me.znepb.zrm.render

import me.znepb.zrm.Main
import me.znepb.zrm.block.post.AbstractPostMountableBlockEntity
import me.znepb.zrm.block.signals.AbstractTrafficSignalBlockEntity
import me.znepb.zrm.block.signals.SignalLight
import me.znepb.zrm.util.PostThickness
import me.znepb.zrm.util.RenderUtils
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction

abstract class AbstractFiveHeadSignalBlockRenderer(
    private val ctx: BlockEntityRendererFactory.Context,
    private val redSignalLight: SignalLight,
    private val rightYellowSignalLight: SignalLight,
    private val rightGreenSignalLight: SignalLight,
    private val leftYellowSignalLight: SignalLight,
    private val leftGreenSignalLight: SignalLight
) :
    AbstractPostMountableRenderer<AbstractTrafficSignalBlockEntity>() {

    companion object {
        val SIGNAL_FRAME_5 = Main.ModId("block/signal_frame_5")
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
        val thickest = AbstractPostMountableBlockEntity.getThickest(entity)
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
        RenderUtils.renderModel(matrices, renderer.buffer, light, overlay, SIGNAL_FRAME_5, null)
        matrices.pop()

        renderer.renderSignal(redSignalLight, 0.0, 0.25)
        renderer.renderSignal(leftYellowSignalLight, -0.125, 0.0)
        renderer.renderSignal(leftGreenSignalLight, -0.125, -0.25)
        renderer.renderSignal(rightYellowSignalLight, 0.125, 0.0)
        renderer.renderSignal(rightGreenSignalLight, 0.125, -0.25)
    }
}