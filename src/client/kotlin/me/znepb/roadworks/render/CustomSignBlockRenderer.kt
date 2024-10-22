package me.znepb.roadworks.render

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import me.znepb.roadworks.block.sign.CustomSignBlockEntity
import me.znepb.roadworks.util.Charset
import me.znepb.roadworks.util.RenderUtils.drawSquare
import me.znepb.roadworks.util.RenderUtils.nineSplice
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.*
import net.minecraft.client.render.RenderLayer.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import java.util.*

@Environment(EnvType.CLIENT)
class CustomSignBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    AbstractPostMountableRenderer<CustomSignBlockEntity>() {

    companion object {
        val SIGN_TEX_HEIGHT = 8
        val SIGN_TEX_WIDTH = 8
        val SIGN_CORNER_SIZE = 3F

        val FRONT_TEXTURES = mutableMapOf<String, Identifier>().also {
            it["green"] = RoadworksMain.ModId("textures/block/signs/background_green.png")
            it["yellow"] = RoadworksMain.ModId("textures/block/signs/background_yellow.png")
        }
        val BACK_TEXTURE = RoadworksMain.ModId("textures/block/signs/back_full.png")
    }


    private fun getRenderLayer(texture: Identifier): RenderLayer {
        // Solid
        of(
            "solid",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            DrawMode.QUADS,
            2097152,
            true,
            false,
            MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).program(SOLID_PROGRAM).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true)
        );
        // Entity Translucent
        val multiPhaseParameters = MultiPhaseParameters.builder().program(ENTITY_TRANSLUCENT_CULL_PROGRAM)
            .texture(RenderPhase.Texture(texture, false, false)).transparency(
                TRANSLUCENT_TRANSPARENCY
            ).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true)
        of(
            "entity_translucent_cull",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            DrawMode.QUADS,
            256,
            true,
            true,
            multiPhaseParameters
        )

        return of(
            "custom_sign_layer",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            DrawMode.QUADS,
            2097152,
            true,
            false,
            MultiPhaseParameters.builder()
                .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                .program(RenderPhase.CUTOUT_MIPPED_PROGRAM)
                .texture(RenderPhase.Texture(texture, false, true))
                .transparency(RenderPhase.NO_TRANSPARENCY)
                .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                .build(true)
        )
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
        val contents = entity.contents
        val color = entity.color

        // Get size of contents
        var size = -1
        contents.forEach {
            size += (it.w + 1)
        }

        val pixelCount = size.toFloat()
        val frontTexture = if(FRONT_TEXTURES[color] != null) FRONT_TEXTURES[color] else Identifier("")

        // Prepare matrices
        matrices.push()
        matrices.translate(0.5F, 0.5F, 0.5F)
        matrices.multiply(Direction.byId(entity.facing).rotationQuaternion.rotateXYZ((Math.PI / 2).toFloat(), Math.PI.toFloat(), Math.PI.toFloat()))
        matrices.translate(-0.5F, -0.5F, -0.5F)
        matrices.translate(0.0F, 0.0F, (maxThickness.id.toFloat() / 16) + 0.0078125F)

        // Render sign background
        val buffer: VertexConsumer = vertexConsumers.getBuffer(this.getRenderLayer(frontTexture!!))
        val matrix = matrices.peek().positionMatrix

        nineSplice(
            (32F - ((pixelCount + 8) / 2)), 24F, 0.5F, pixelCount + 8, 16F,
            SIGN_CORNER_SIZE, SIGN_CORNER_SIZE, SIGN_CORNER_SIZE, SIGN_CORNER_SIZE, 64, 64,
            SIGN_TEX_WIDTH, SIGN_TEX_HEIGHT, buffer, matrix, light, overlay
        )

        // Render sign text
        val charsetBuffer: VertexConsumer = vertexConsumers.getBuffer(this.getRenderLayer(Charset.TEXTURE))
        var x = 32F - (pixelCount / 2)
        contents.forEach {
            drawSquare(
                x, 28F, 0.501F, it.x * 8F, it.y * 8F, it.w.toFloat(), 8F,
                64, 64, it.w.toFloat(), 8F,
                Charset.CHARSET_WIDTH, Charset.CHARSET_HEIGHT, charsetBuffer, matrix, light, overlay
            )

            x += it.w + 1
        }

        matrices.pop()

        // Render sign back
        matrices.push()

        matrices.translate(0.5F, 0.5F, 0.5F)
        matrices.multiply(Direction.byId(entity.facing).rotationQuaternion.rotateXYZ((Math.PI / 2).toFloat(), 0.0F, Math.PI.toFloat()))
        matrices.translate(-0.5F, -0.5F, -0.5F)

        val backBuffer: VertexConsumer = vertexConsumers.getBuffer(this.getRenderLayer(BACK_TEXTURE))
        nineSplice(
            (32F - ((pixelCount + 8) / 2)), 24F, 0.5F, pixelCount + 8, 16F,
            SIGN_CORNER_SIZE, SIGN_CORNER_SIZE, SIGN_CORNER_SIZE, SIGN_CORNER_SIZE, 64, 64,
            64, 64, backBuffer, matrices.peek().positionMatrix, light, overlay
        )

        matrices.pop()
    }

}