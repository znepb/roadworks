package me.znepb.roadworks.render

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksMain.SIGN_TYPES
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import me.znepb.roadworks.block.sign.SignBlockEntity
import me.znepb.roadworks.block.sign.custom.CustomSignBlockEntity
import me.znepb.roadworks.util.Charset
import me.znepb.roadworks.util.PostThickness
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter.CULL_FACE_EPSILON
import net.minecraft.client.MinecraftClient
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPart.Vertex
import net.minecraft.client.render.*
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import org.joml.Matrix4f
import org.joml.Quaternionf
import java.util.*

class CustomSignBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    AbstractPostMountableRenderer<CustomSignBlockEntity>() {

    companion object {
        val NORTH_ONLY = EnumSet.of(Direction.NORTH)
        val SOUTH_ONLY = EnumSet.of(Direction.SOUTH)

        val WALL_SIGN_FRONT = ModelPart.Cuboid(0, 0, 0F, 0F, 15.99F, 16F, 16F, 0F, 0F, 0F, 0F, false, 16F, 16F, NORTH_ONLY)
        val POST_SIGN_NONE_FRONT = ModelPart.Cuboid(0, 0, 0F, 0F, 8F, 16F, 16F, 0F, 0F, 0F, 0F, false, 16F, 16F, NORTH_ONLY)
        val POST_SIGN_THIN_FRONT = ModelPart.Cuboid(0, 0, 0F, 0F, 6.99F, 16F, 16F, 0F, 0F, 0F, 0F, false, 16F, 16F, NORTH_ONLY)
        val POST_SIGN_MEDIUM_FRONT = ModelPart.Cuboid(0, 0, 0F, 0F, 5.99F, 16F, 16F, 0F, 0F, 0F, 0F, false, 16F, 16F, NORTH_ONLY)
        val POST_SIGN_THICK_FRONT = ModelPart.Cuboid(0, 0, 0F, 0F, 4.99F, 16F, 16F, 0F, 0F, 0F, 0F, false, 16F, 16F, NORTH_ONLY)
        val WALL_SIGN_BACK = ModelPart.Cuboid(0, 0, 0F, 0F, 15.99F, 16F, 16F, 0F, 0F, 0F, 0F, false, 16F, 16F, SOUTH_ONLY)
        val POST_SIGN_NONE_BACK = ModelPart.Cuboid(0, 0, 0F, 0F, 8F, 16F, 16F, 0F, 0F, 0F, 0F, false, 16F, 16F, SOUTH_ONLY)
        val POST_SIGN_THIN_BACK = ModelPart.Cuboid(0, 0, 0F, 0F, 6.99F, 16F, 16F, 0F, 0F, 0F, 0F, false, 16F, 16F, SOUTH_ONLY)
        val POST_SIGN_MEDIUM_BACK = ModelPart.Cuboid(0, 0, 0F, 0F, 5.99F, 16F, 16F, 0F, 0F, 0F, 0F, false, 16F, 16F, SOUTH_ONLY)
        val POST_SIGN_THICK_BACK = ModelPart.Cuboid(0, 0, 0F, 0F, 4.99F, 16F, 16F, 0F, 0F, 0F, 0F, false, 16F, 16F, SOUTH_ONLY)

        val CORNER_SIZE = 0.0625F
        val EDGE_TOP_LEFT = 0.5F
        val EDGE_TOP_RIGHT = 0.625F
        val EDGE_HEIGHT = 0.5F
        val ONE_SIGN_PIXEL = 0.015625F
        val ONE_CHARSET_PIXEL = 0.0078125F
    }

    private fun createVertex(x: Float, y: Float, u: Float, v: Float, buffer: VertexConsumer, matrix: Matrix4f, light: Int, overlay: Int, z: Float) {
        buffer.vertex(matrix, x, y, z)     // Position
            .color(255, 255, 255, 255)              // Color
            .texture(u, v)                    // Texture coordinates
            .overlay(overlay)                       // Overlay coordinates (default 0 for no effect)
            .light(light)                           // Light coordinates
            .normal(0.0F, 0.0F, 1.0F)              // Normal (optional, for lighting calculation)
            .next()
    }


    private fun createVertex(x: Float, y: Float, u: Float, v: Float, buffer: VertexConsumer, matrix: Matrix4f, light: Int, overlay: Int) {
        createVertex(x, y, u, v, buffer, matrix, light, overlay, 0.5F)
    }

    private fun renderSignCorner(x1: Float, x2: Float, y1: Float, y2: Float, u1: Float, u2: Float, v1: Float, v2: Float, buffer: VertexConsumer, matrix: Matrix4f, light: Int, overlay: Int) {
        createVertex(x1, y1, u1, v1, buffer, matrix, light, overlay)
        createVertex(x2, y1, u2, v1, buffer, matrix, light, overlay)
        createVertex(x2, y2, u2, v2, buffer, matrix, light, overlay)
        createVertex(x1, y2, u1, v2, buffer, matrix, light, overlay)
    }

    private fun renderSignBackground(x1: Float, y1: Float, x2: Float, y2: Float, buffer: VertexConsumer, matrix: Matrix4f, light: Int, overlay: Int) {
        // Render corners
        // TL
        renderSignCorner(
            x1, x1 + CORNER_SIZE, y1, y1 + CORNER_SIZE, 0.0F, 0.5F, 0.0F, 0.5F,
            buffer, matrix, light, overlay
        )
        // TR
        renderSignCorner(
            x2 - CORNER_SIZE, x2, y1, y1 + CORNER_SIZE, 0.5F, 1F, 0.0F, 0.5F,
            buffer, matrix, light, overlay
        )
        // BL
        renderSignCorner(
            x1, x1 + CORNER_SIZE, y2 - CORNER_SIZE, y2, 0.0F, 0.5F, 0.5F, 1F,
            buffer, matrix, light, overlay
        )
        // BR
        renderSignCorner(
            x2 - CORNER_SIZE, x2, y2 - CORNER_SIZE, y2, 0.5F, 1.0F, 0.5F, 1F,
            buffer, matrix, light, overlay
        )

        // Bottom
        createVertex(x1 + CORNER_SIZE, y1, EDGE_TOP_LEFT, 0.0F, buffer, matrix, light, overlay)
        createVertex(x2 - CORNER_SIZE, y1, EDGE_TOP_RIGHT, 0.0F, buffer, matrix, light, overlay)
        createVertex(x2 - CORNER_SIZE, y1 + CORNER_SIZE, EDGE_TOP_RIGHT, EDGE_HEIGHT, buffer, matrix, light, overlay)
        createVertex(x1 + CORNER_SIZE, y1 + CORNER_SIZE, EDGE_TOP_LEFT, EDGE_HEIGHT, buffer, matrix, light, overlay)

        // Top
        createVertex(x1 + CORNER_SIZE, y2 - CORNER_SIZE, EDGE_TOP_LEFT, EDGE_HEIGHT, buffer, matrix, light, overlay)
        createVertex(x2 - CORNER_SIZE, y2 - CORNER_SIZE, EDGE_TOP_RIGHT, EDGE_HEIGHT, buffer, matrix, light, overlay)
        createVertex(x2 - CORNER_SIZE, y2, EDGE_TOP_RIGHT, 1.0F, buffer, matrix, light, overlay)
        createVertex(x1 + CORNER_SIZE, y2, EDGE_TOP_LEFT, 1.0F, buffer, matrix, light, overlay)

        // Left
        createVertex(x1, y1 + CORNER_SIZE,0.0F, EDGE_TOP_LEFT, buffer, matrix, light, overlay)
        createVertex(x1 + CORNER_SIZE, y1 + CORNER_SIZE, EDGE_HEIGHT, EDGE_TOP_LEFT, buffer, matrix, light, overlay)
        createVertex(x1 + CORNER_SIZE, y2 - CORNER_SIZE, EDGE_HEIGHT, EDGE_TOP_RIGHT, buffer, matrix, light, overlay)
        createVertex(x1, y2 - CORNER_SIZE, 0.0F, EDGE_TOP_RIGHT, buffer, matrix, light, overlay)

        // Right
        createVertex(x2 - CORNER_SIZE, y1 + CORNER_SIZE, EDGE_HEIGHT, EDGE_TOP_LEFT, buffer, matrix, light, overlay)
        createVertex(x2, y1 + CORNER_SIZE, 1.0F, EDGE_TOP_LEFT, buffer, matrix, light, overlay)
        createVertex(x2, y2 - CORNER_SIZE, 1.0F, EDGE_TOP_RIGHT, buffer, matrix, light, overlay)
        createVertex(x2 - CORNER_SIZE, y2 - CORNER_SIZE, EDGE_HEIGHT, EDGE_TOP_RIGHT, buffer, matrix, light, overlay)

        // Innerds
        createVertex(x1 + CORNER_SIZE, y1 + CORNER_SIZE, 0.375F, 0.375F, buffer, matrix, light, overlay)
        createVertex(x2 - CORNER_SIZE, y1 + CORNER_SIZE, 0.625F, 0.375F, buffer, matrix, light, overlay)
        createVertex(x2 - CORNER_SIZE, y2 - CORNER_SIZE, 0.625F, 0.625F, buffer, matrix, light, overlay)
        createVertex(x1 + CORNER_SIZE, y2 - CORNER_SIZE, 0.375F, 0.625F, buffer, matrix, light, overlay)
    }

    override fun renderAttachment(
        entity: CustomSignBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val maxThickness = AbstractPostMountableBlockEntity.getThickest(entity)
        val layer = vertexConsumers.getBuffer(RenderLayers.getBlockLayer(entity.cachedState))

        val contents = entity.contents
        val color = entity.color

        val texture = RoadworksMain.ModId("textures/block/signs/background_$color.png")
        val charset = RoadworksMain.ModId("textures/block/signs/charset.png")

        var size = -1
        contents.forEach {
            size += (it.w + 1)
        }

        val pixelCount = size.toFloat() * ONE_SIGN_PIXEL

        // Render sign background

        MinecraftClient.getInstance().textureManager.bindTexture(texture)
        val buffer: VertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture))
        matrices.push()

        matrices.translate(0.5F, 0.5F, 0.5F)
        matrices.multiply(Direction.byId(entity.facing).rotationQuaternion.rotateXYZ((Math.PI / 2).toFloat(), Math.PI.toFloat(), Math.PI.toFloat()))

        matrices.translate(-0.5F, -0.5F, -0.5F)
        matrices.translate(0.0F, 0.0F, (maxThickness.id.toFloat() / 16) + 0.0078125F)
        val matrix = matrices.peek().positionMatrix

        renderSignBackground((0.5F - ((pixelCount + 0.125F) / 2)), 0.375F, (0.5F + ((pixelCount + 0.125F) / 2)), 0.625F, buffer, matrix, light, overlay)

        // Render sign text
        MinecraftClient.getInstance().textureManager.bindTexture(charset)
        val charsetBuffer: VertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(charset))
        var x = 0.5F - (pixelCount / 2)
        contents.forEach {
            val signW = it.w * ONE_SIGN_PIXEL
            val charsetW = it.w * ONE_CHARSET_PIXEL

            val sX = x
            val endX = x + signW
            val u1 = (it.x * 8).toFloat() / 128
            val v1 = (it.y * 8).toFloat() / 128
            val u2 = u1 + charsetW
            val v2 = v1 + ONE_CHARSET_PIXEL * 8

            createVertex(sX, 0.4375F, u1, v2, charsetBuffer, matrix, light, overlay, 0.501F)
            createVertex(endX, 0.4375F, u2, v2, charsetBuffer, matrix, light, overlay, 0.501F)
            createVertex(endX, 0.5625F, u2, v1, charsetBuffer, matrix, light, overlay, 0.501F)
            createVertex(sX, 0.5625F, u1, v1, charsetBuffer, matrix, light, overlay, 0.501F)

            x += (signW + ONE_SIGN_PIXEL)
        }

        matrices.pop()
    }

}