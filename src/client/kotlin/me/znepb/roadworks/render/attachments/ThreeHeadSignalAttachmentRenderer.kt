package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.signal.ThreeHeadSignalAttachment
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3d

class ThreeHeadSignalAttachmentRenderer : AttachmentRenderer<ThreeHeadSignalAttachment> {
    companion object {
        val SIGNAL_FRAME_3 = RoadworksMain.ModId("block/signal_frame_3")
    }

    override fun render(
        attachment: ThreeHeadSignalAttachment,
        blockEntity: AttachmentContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        offset: Vector3d
    ) {
        val renderer = SignalRenderer(attachment, matrices, vertexConsumers, light, overlay)

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing.opposite, 0)
        matrices.translate(offset.x, offset.y, -offset.z)
        RenderUtils.renderModel(matrices, renderer.buffer, light, overlay, SIGNAL_FRAME_3, null)
        matrices.pop()

        renderer.renderSignal(attachment.signalType.getReds()[0], 0.0, 0.25, offset)
        renderer.renderSignal(attachment.signalType.getYellows()[0], 0.0, 0.0, offset)
        renderer.renderSignal(attachment.signalType.getGreens()[0], 0.0, -0.25, offset)
    }
}