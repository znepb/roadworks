package me.znepb.roadworks.render

import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.block.PedestrianButton
import me.znepb.roadworks.block.PedestrianButtonBlockEntity
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity.Companion.getThickest
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction

class PedestrianButtonRenderer(
    private val ctx: BlockEntityRendererFactory.Context,
) :
    AbstractPostMountableRenderer<PedestrianButtonBlockEntity>() {

    companion object {
        val PEDESTRIAN_BUTTON_OFF = ModId("block/pedestrian_button_off")
        val PEDESTRIAN_BUTTON_ON = ModId("block/pedestrian_button_on")
    }

    override fun renderAttachment(
        entity: PedestrianButtonBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {

        val direction = Direction.byId(entity.facing)
        val thickest = getThickest(entity)
        val translateBy = if(entity.wall) {
            7.0/16
        } else {
            -(when (thickest) {
                PostThickness.NONE -> 0
                PostThickness.THIN -> 2
                PostThickness.MEDIUM -> 3
                PostThickness.THICK -> 3
            }).toDouble() / 16
        }.toDouble()

        val isButtonPressed = entity.world?.getBlockState(entity.pos)?.block is PedestrianButton && entity.world?.getBlockState(entity.pos)?.get(Properties.POWERED) == true

        val renderer = PostMountRenderer(entity, matrices, vertexConsumers, light, overlay, direction, translateBy)
        matrices.push()
        renderer.rotateForRender()
        matrices.translate(0.0, 0.0, translateBy)
        RenderUtils.renderModel(matrices, renderer.buffer, light, overlay, if(isButtonPressed) PEDESTRIAN_BUTTON_ON else PEDESTRIAN_BUTTON_OFF, null)
        matrices.pop()

    }
}