package me.znepb.roadworks.util

import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random

class RenderUtils {
    companion object{
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
            val random = Random.create(0);

            random.setSeed(42L)
            renderQuads(transform, renderer, lightmapCoord, overlayLight, model.getQuads(null as BlockState?, direction as Direction?, random))
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
            return Math.toRadians(when(direction) {
                Direction.NORTH -> 180.0
                Direction.WEST -> 90.0
                Direction.SOUTH -> 0.0
                Direction.EAST -> 270.0
                else -> 0.0
            }).toFloat()
        }
    }
}