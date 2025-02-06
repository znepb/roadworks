package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.attachment.Attachment
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.render.ContainerRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import org.joml.Vector3d
import org.joml.Vector3f

interface AttachmentRenderer<T : Attachment> {
    fun render(
        attachment: T,
        blockEntity: AttachmentContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        offset: Vector3d
    )

    companion object {
        fun translateForCenter(matrices: MatrixStack, facing: Direction, faceRotation: Int) {
            matrices.translate(0.5F, 0.5F, 0.5F)
            matrices.multiply(facing.rotationQuaternion.rotateXYZ((Math.PI / 2).toFloat(), Math.PI.toFloat(), Math.PI.toFloat() + Math.toRadians(faceRotation.toDouble()).toFloat()))
            matrices.translate(-0.5F, -0.5F, -0.5F)
        }
    }
}