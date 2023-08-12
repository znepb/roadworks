package me.znepb.zrm.render

import me.znepb.zrm.block.post.AbstractPostMountableBlockEntity
import me.znepb.zrm.util.PostThickness
import me.znepb.zrm.util.RenderUtils
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction

abstract class AbstractPostMountableRenderer<T : AbstractPostMountableBlockEntity> : BlockEntityRenderer<T> {
    private fun addSideThickness(
        blockEntity: T,
        direction: Direction,
        connectionSize: Int,
        matrices: MatrixStack,
        buffer: VertexConsumer,
        light: Int,
        overlay: Int,
    ) {
        if(connectionSize == 0) return
        if(direction.id == blockEntity.facing) return

        val sizeModel = when(connectionSize) {
            1 -> PostBlockRenderer.POST_THIN_EXT_MODEL
            3 -> PostBlockRenderer.POST_THICK_EXT_MODEL
            else -> PostBlockRenderer.POST_MEDIUM_EXT_MODEL
        }

        matrices.push()
        matrices.multiply(direction.rotationQuaternion, 0.5F, 0.5F, 0.5F)
        RenderUtils.renderModel(matrices, buffer, light, overlay, sizeModel, null)
        matrices.pop()
    }

    override fun render(
        entity: T,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val buffer: VertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull())

        val maxThickness = AbstractPostMountableBlockEntity.getThickest(entity)

        if(!entity.wall) {
            val midsectionModel = when(maxThickness) {
                PostThickness.THICK -> PostBlockRenderer.POST_THICK_MID_MODEL
                PostThickness.MEDIUM -> PostBlockRenderer.POST_MEDIUM_MID_MODEL
                PostThickness.THIN -> PostBlockRenderer.POST_THIN_MID_MODEL
                else -> null
            }

            if(midsectionModel != null) {
                RenderUtils.renderModel(matrices, buffer, light, overlay, midsectionModel, null)
            }

            addSideThickness(entity, Direction.NORTH, entity.north, matrices, buffer, light, overlay)
            addSideThickness(entity, Direction.EAST, entity.east, matrices, buffer, light, overlay)
            addSideThickness(entity, Direction.SOUTH, entity.south, matrices, buffer, light, overlay)
            addSideThickness(entity, Direction.WEST, entity.west, matrices, buffer, light, overlay)
            addSideThickness(entity, Direction.UP, entity.up, matrices, buffer, light, overlay)
            addSideThickness(entity, Direction.DOWN, entity.down, matrices, buffer, light, overlay)
        }

        this.renderAttachment(entity, tickDelta, matrices, vertexConsumers, light, overlay)
    }

    abstract fun renderAttachment(entity: T,
                                  tickDelta: Float,
                                  matrices: MatrixStack,
                                  vertexConsumers: VertexConsumerProvider,
                                  light: Int,
                                  overlay: Int)
}