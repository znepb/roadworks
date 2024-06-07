package me.znepb.roadworks.render

import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity.Companion.getThickest
import me.znepb.roadworks.block.signals.AbstractTrafficSignalBlockEntity
import me.znepb.roadworks.block.signals.SignalLight
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction

class PedestrianSignalRenderer(
    private val ctx: BlockEntityRendererFactory.Context,
) :
    AbstractPostMountableRenderer<AbstractTrafficSignalBlockEntity>() {

    companion object {
        val WALK = ModId("block/pedestrian_walk")
        val DONT_WALK = ModId("block/pedestrian_dont_walk")
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
            6.0/16
        } else {
            -(when (thickest) {
                PostThickness.NONE -> 0
                PostThickness.THIN -> 2
                PostThickness.MEDIUM -> 3
                PostThickness.THICK -> 5
            }).toDouble() / 16
        }.toDouble()

        val renderer = PostMountRenderer(entity, matrices, vertexConsumers, light, overlay, direction, translateBy)

        matrices.push()
        renderer.rotateForRender()
        matrices.translate(0.0, 0.0, translateBy)
        RenderUtils.renderModel(matrices, renderer.buffer, light, overlay, if (entity.getSignal(SignalLight.WALK)) WALK else DONT_WALK, null)
        matrices.pop()
    }
}