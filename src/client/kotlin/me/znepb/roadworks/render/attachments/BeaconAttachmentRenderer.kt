package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.attachment.AttachmentPosition
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.render.ContainerRenderer
import me.znepb.roadworks.signal.BeaconAttachment
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3d

class BeaconAttachmentRenderer : AttachmentRenderer<BeaconAttachment> {
    companion object {
        val SIGNAL_FRAME_1 = RoadworksMain.ModId("block/signal_frame_1")
    }

    override fun render(
        attachment: BeaconAttachment,
        blockEntity: AttachmentContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        offset: Vector3d
    ) {
        val renderer = SignalRenderer(attachment, matrices, vertexConsumers, light, overlay)

        val offsetHeight = when(attachment.position) {
            AttachmentPosition.TOP -> 0.275
            AttachmentPosition.MIDDLE -> 0.0
            AttachmentPosition.BOTTOM -> -0.275
        }

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing.opposite, 0)
        matrices.translate(0.0, offsetHeight, -offset.z)
        RenderUtils.renderModel(matrices, renderer.buffer, light, overlay, SIGNAL_FRAME_1, null)
        matrices.pop()

        renderer.renderSignal(attachment.signalType.lights[0], 0.0, offsetHeight, offset)
    }
}