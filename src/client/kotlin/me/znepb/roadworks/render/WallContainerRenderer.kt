package me.znepb.roadworks.render

import me.znepb.roadworks.container.WallContainerBlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3d

class WallContainerRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    ContainerRenderer<WallContainerBlockEntity>(ctx) {
    override fun render(
        entity: WallContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        this.renderAttachments(entity, tickDelta, matrices, vertexConsumers, light, overlay, Vector3d(0.0, 0.0, -0.5))
    }
}