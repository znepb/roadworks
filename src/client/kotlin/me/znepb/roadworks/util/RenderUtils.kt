package me.znepb.roadworks.util

import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import org.joml.Matrix4f

object RenderUtils {
    fun renderModel(
        transform: MatrixStack,
        renderer: VertexConsumer,
        lightmapCoord: Int,
        overlayLight: Int,
        modelLocation: Identifier,
        direction: Direction?
    ) {
        val model = MinecraftClient.getInstance().bakedModelManager.getModel(modelLocation)

        if(model != null) {
            renderModel(transform, renderer, lightmapCoord, overlayLight, model, direction)
        } else {
            renderModel(transform, renderer, lightmapCoord, overlayLight, MinecraftClient.getInstance().bakedModelManager.missingModel, direction)
        }
    }

    fun renderModel(
        transform: MatrixStack,
        renderer: VertexConsumer,
        lightmapCoord: Int,
        overlayLight: Int,
        model: BakedModel,
        direction: Direction?
    ) {
        val random = Random.create(0)

        random.setSeed(42L)
        renderQuads(transform, renderer, lightmapCoord, overlayLight, model.getQuads(null as BlockState?,
            direction, random))
    }

    fun renderQuads(transform: MatrixStack, buffer: VertexConsumer, lightmapCoord: Int, overlayLight: Int, quads: List<BakedQuad>) {
        val matrix = transform.peek()
        val quads: Iterator<*> = quads.iterator()
        while (quads.hasNext()) {
            val bakedquad = quads.next() as BakedQuad
            buffer.quad(matrix, bakedquad, 1F, 1F, 1F, lightmapCoord, overlayLight)
        }
    }

    fun getRotationFromDirection(direction: Direction): Float {
        return Math.toRadians(
            when (direction) {
                Direction.NORTH -> 180.0
                Direction.WEST -> 90.0
                Direction.SOUTH -> 0.0
                Direction.EAST -> 270.0
                else -> 0.0
            }
        ).toFloat()
    }

    fun createVertex(x: Float, y: Float, u: Float, v: Float, buffer: VertexConsumer, matrix: Matrix4f, light: Int, overlay: Int, z: Float) {
        buffer.vertex(matrix, x, y, z)     // Position
            .color(255, 255, 255, 255)              // Color
            .texture(u, v)                    // Texture coordinates
            .overlay(OverlayTexture.DEFAULT_UV)                       // Overlay coordinates (default 0 for no effect)
            .light(light)                           // Light coordinates
            .normal(x, y, z)              // Normal (optional, for lighting calculation)
            .next()
    }

    fun drawSquare(
        x: Float, y: Float, z: Float, u: Float, v: Float, width: Float, height: Float,
        blockWidth: Int, blockHeight: Int, regionWidth: Float, regionHeight: Float,
        textureWidth: Int, textureHeight: Int, buffer: VertexConsumer, matrix: Matrix4f, light: Int, overlay: Int
    ) {
        val textureWidthFloat = textureWidth.toFloat()
        val textureHeightFloat = textureHeight.toFloat()
        val blockWidthFloat = blockWidth.toFloat()
        val blockHeightFloat = blockHeight.toFloat()

        val x1 = x / blockWidthFloat
        val x2 = (x / blockWidthFloat) + (width / blockWidthFloat)
        val y1 = y / blockHeightFloat
        val y2 = (y / blockHeightFloat) + (height / blockHeightFloat)

        val u1 = u / textureWidthFloat
        val u2 = (u / textureWidthFloat) + (regionWidth / textureWidthFloat)
        val v1 = v / textureHeightFloat
        val v2 = (v / textureHeightFloat) + (regionHeight / textureWidthFloat)

        createVertex(x1, y1, u1, v2, buffer, matrix, light, overlay, z)
        createVertex(x2, y1, u2, v2, buffer, matrix, light, overlay, z)
        createVertex(x2, y2, u2, v1, buffer, matrix, light, overlay, z)
        createVertex(x1, y2, u1, v1, buffer, matrix, light, overlay, z)
    }

    fun nineSplice(x1: Float, y1: Float, z: Float, width: Float, height: Float,
                   left: Float, right: Float, top: Float, bottom: Float, blockWidth: Int, blockHeight: Int,
                   textureWidth: Int, textureHeight: Int, buffer: VertexConsumer, matrix: Matrix4f, light: Int,
                   overlay: Int
    ) {
        val x2 = x1 + width
        val y2 = y1 + height

        val innerRegionWidth = (textureWidth - right - left).coerceAtMost(width - right - left)
        val innerRegionHeight = (textureHeight - top - bottom).coerceAtMost(height - top - bottom)

        // Render corners
        // Top Left
        drawSquare(
            x1, y2 - top, z,
            0F, 0F,
            left, top,
            blockWidth, blockHeight,
            left, top,
            textureWidth,
            textureHeight,
            buffer, matrix, light, overlay
        )

        // Top RIght
        drawSquare(
            x2 - right, y2 - top, z,
            textureWidth - right, 0F,
            right, top,
            blockWidth, blockHeight,
            right, top,
            textureWidth,
            textureHeight,
            buffer, matrix, light, overlay
        )

        // Bottom Left
        drawSquare(
            x1, y1, z,
            0F, textureHeight - bottom,
            left, bottom,
            blockWidth, blockHeight,
            left, bottom,
            textureWidth,
            textureHeight,
            buffer, matrix, light, overlay
        )

        // Bottom Right
        drawSquare(
            x2 - right, y1, z,
            textureWidth - right, textureHeight - bottom,
            right, bottom,
            blockWidth, blockHeight,
            right, bottom,
            textureWidth,
            textureHeight,
            buffer, matrix, light, overlay
        )

        // Top Line
        drawSquare(
            x1 + left, y2 - top, z,
            left, 0F,
            width - right - left, top,
            blockWidth, blockHeight,
            innerRegionWidth, top,
            textureWidth,
            textureHeight,
            buffer, matrix, light, overlay
        )

        // Bottom Line
        drawSquare(
            x1 + left, y1, z,
            left, textureHeight - bottom,
            width - right - left, bottom,
            blockWidth, blockHeight,
            innerRegionWidth, bottom,
            textureWidth,
            textureHeight,
            buffer, matrix, light, overlay
        )

        // Left Line
        drawSquare(
            x1, y1 + bottom, z,
            0.0F, top,
            left, height - top - bottom,
            blockWidth, blockHeight,
            left, innerRegionHeight,
            textureWidth,
            textureHeight,
            buffer, matrix, light, overlay
        )

        // Right Line
        drawSquare(
            x2 - right, y1 + bottom, z,
            textureWidth - right, top,
            right, height - top - bottom,
            blockWidth, blockHeight,
            right, innerRegionHeight,
            textureWidth,
            textureHeight,
            buffer, matrix, light, overlay
        )

        // Innards
        drawSquare(
            x1 + left, y1 + bottom, z,
            left, top,
            width - left - right, height - top - bottom,
            blockWidth, blockHeight,
            innerRegionWidth, innerRegionHeight,
            textureWidth,
            textureHeight,
            buffer, matrix, light, overlay
        )
    }
}