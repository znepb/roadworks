package me.znepb.roadworks.render

import me.znepb.roadworks.RoadworksClient
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainer
import me.znepb.roadworks.container.PostContainerBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.joml.Vector3d

abstract class ContainerRenderer<T : BlockEntity>(private val ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<T> {
    fun renderOutline(
        blockEntity: PostContainerBlockEntity,
        matrices: MatrixStack,
        vertexConsumer: VertexConsumer,
        entity: Entity,
        world: World,
        cameraX: Double,
        cameraY: Double,
        cameraZ: Double,
        pos: BlockPos,
        state: BlockState
    ) {
        val block = state.block
        if (block !is PostContainer) return
        val client: MinecraftClient = MinecraftClient.getInstance()
        val hit = client.crosshairTarget

        if (hit?.type == HitResult.Type.BLOCK) {
            val blockHit = hit as BlockHitResult
            val shapeContext = ShapeContext.of(entity)
            if (blockHit.blockPos != pos) return

            val hitAttachment = blockEntity.getAttachmentHit(blockHit)

            if (hitAttachment != null) {
                WorldRenderer.drawCuboidShapeOutline(
                    matrices,
                    vertexConsumer,
                    hitAttachment.getShape(shapeContext),
                    pos.x.toDouble() - cameraX,
                    pos.y.toDouble() - cameraY,
                    pos.z.toDouble() - cameraZ,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.4f
                )
                return
            }

            val shape = block.getShape(world, pos, shapeContext)
            WorldRenderer.drawCuboidShapeOutline(
                matrices,
                vertexConsumer,
                shape,
                pos.x.toDouble() - cameraX,
                pos.y.toDouble() - cameraY,
                pos.z.toDouble() - cameraZ,
                0.0f,
                0.0f,
                0.0f,
                0.4f
            )
        }
    }

    fun renderAttachments(
        blockEntity: AttachmentContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        offset: Vector3d
    ) {
        blockEntity.attachments.forEach {
            RoadworksClient.attachmentRenderers[it.type]?.render(
                it, blockEntity, tickDelta, matrices, vertexConsumers, light, overlay, offset
            )
        }
    }
}