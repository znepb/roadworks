package me.znepb.roadworks.render.attachments

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainer
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.signal.PedestrianSignalAttachment
import me.znepb.roadworks.signal.SignalLight
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3d

class PedestrianSignalAttachmentRenderer : AttachmentRenderer<PedestrianSignalAttachment> {
    companion object {
        val BLANK_SIGNAL = RoadworksMain.ModId("block/pedestrian_signal")
        val WALK = RoadworksMain.ModId("block/pedestrian_walk")
        val DONT_WALK = RoadworksMain.ModId("block/pedestrian_dont_walk")
    }

    override fun render(
        attachment: PedestrianSignalAttachment,
        blockEntity: AttachmentContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        offset: Vector3d
    ) {
        val renderer = SignalRenderer(attachment, matrices, vertexConsumers, light, overlay)
        val signal = if (attachment.isSignalActive(SignalLight.WALK)) WALK
        else if(attachment.isSignalActive(SignalLight.DONT_WALK)) DONT_WALK
        else BLANK_SIGNAL

        var offsetZ = 0.0
        if(attachment.container is PostContainerBlockEntity && (attachment.container as PostContainerBlockEntity).thickness === PostThickness.THICK) {
            offsetZ = -1.0 / 16.0
        }

        matrices.push()
        AttachmentRenderer.translateForCenter(matrices, attachment.facing.opposite, 0)
        matrices.translate(offset.x, offset.y, -offset.z - 1.0 / 16.0 + offsetZ)
        RenderUtils.renderModel(matrices, renderer.buffer, light, overlay, signal, null)
        matrices.pop()
    }
}