package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.train.CrossingGateAttachment
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3d

class CrossingGateAttachmentRenderer : AttachmentRenderer<CrossingGateAttachment> {
    companion object {
        val HINGE = RoadworksMain.ModId("block/crossing_arm_hinge")
        val GATE_ARM = RoadworksMain.ModId("block/crossing_gate_arm_extension")
    }


    override fun render(
        attachment: CrossingGateAttachment,
        blockEntity: AttachmentContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        offset: Vector3d
    ) {
        val buffer: VertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull())
        val facing = attachment.facing.opposite
        val progress = attachment.getProgress(tickDelta)

        matrices.push()
        matrices.translate(0.5F, 0.5F, 0.5F)
        matrices.multiply(
            facing.rotationQuaternion.rotateXYZ(
                (Math.PI / 2).toFloat(),
                Math.PI.toFloat(),
                Math.PI.toFloat() + (Math.PI.toFloat() * progress * 0.5F)
            )
        )
        matrices.translate(-0.5F, -0.5F, -0.5F)
        matrices.translate(offset.x, offset.y, -offset.z - 1.0 / 16.0)
        RenderUtils.renderModel(matrices, buffer, light, overlay, HINGE, null)
        matrices.pop()

        if(progress > 0 && progress < 1) {
            for (i in 0..<attachment.getExtensionCount()) {
                matrices.push()
                matrices.translate(0.5F, 0.5F, 0.5F)
                matrices.multiply(
                    facing.rotationQuaternion.rotateXYZ(
                        (Math.PI / 2).toFloat(),
                        Math.PI.toFloat(),
                        Math.PI.toFloat() + (Math.PI.toFloat() * progress * 0.5F)
                    )
                )
                matrices.translate(-0.5F, -0.5F, -0.5F)
                matrices.translate(offset.x + i + 1, offset.y, -offset.z - 1.0 / 16.0)
                RenderUtils.renderModel(matrices, buffer, light, overlay, GATE_ARM, null)
                matrices.pop()
            }
        }
    }
}