package me.znepb.roadworks.render

import me.znepb.roadworks.RoadworksMain.SIGN_TYPES
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import me.znepb.roadworks.block.sign.SignBlockEntity
import me.znepb.roadworks.util.PostThickness
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import java.util.*

class SignBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    AbstractPostMountableRenderer<SignBlockEntity>() {

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
    }

    private fun getSignFrontTexture(entity: SignBlockEntity): Identifier? {
        val tex = SIGN_TYPES[entity.signType]?.frontTexture ?: return null
        return Identifier(tex.namespace, "textures/" + tex.path + ".png")
    }

    private fun getSignBackTexture(entity: SignBlockEntity): Identifier? {
        val tex = SIGN_TYPES[entity.signType]?.backTexture ?: return null
        return Identifier(tex.namespace, "textures/" + tex.path + ".png")
    }

    override fun renderAttachment(
        entity: SignBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val maxThickness = AbstractPostMountableBlockEntity.getThickest(entity)
        val frontTexture = getSignFrontTexture(entity)
        val backTexture = getSignBackTexture(entity)

        val signObjectFront = if(entity.wall) WALL_SIGN_FRONT else {
            when (maxThickness) {
                PostThickness.THICK -> POST_SIGN_THICK_FRONT
                PostThickness.MEDIUM -> POST_SIGN_MEDIUM_FRONT
                PostThickness.THIN -> POST_SIGN_THIN_FRONT
                else -> POST_SIGN_NONE_FRONT
            }
        }

        val signObjectBack = if(entity.wall) WALL_SIGN_BACK else {
            when (maxThickness) {
                PostThickness.THICK -> POST_SIGN_THICK_BACK
                PostThickness.MEDIUM -> POST_SIGN_MEDIUM_BACK
                PostThickness.THIN -> POST_SIGN_THIN_BACK
                else -> POST_SIGN_NONE_BACK
            }
        }

        // Render front
        val front = ModelPart(mutableListOf(signObjectFront), Collections.emptyMap())
        // Goofy ahh texture orientation correcting - should probably be fixed and I should change it to use block models
        // or something, but that's a problem for future me. :D
        when(entity.facing) {
            Direction.EAST.id -> front.setPivot(16F, 16F, 16F)
            Direction.SOUTH.id -> front.setPivot(0F, 16F, 16F)
            Direction.WEST.id -> front.setPivot(0F, 16F, 0F)
            else -> front.setPivot(16F, 16F, 0F)
        }
        front.setAngles(
            0F,
            when(entity.facing) {
                Direction.NORTH.id -> 0F
                Direction.EAST.id -> Math.toRadians(90.0).toFloat()
                Direction.SOUTH.id -> Math.toRadians(180.0).toFloat()
                Direction.WEST.id -> Math.toRadians(270.0).toFloat()
                else -> 0F
            },
            Math.toRadians(180.0).toFloat()
        )

        if(backTexture != null) front.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(backTexture)), light, overlay)

        // Render back
        val back = ModelPart(mutableListOf(signObjectBack), Collections.emptyMap())
        back.copyTransform(front)
        if(frontTexture != null) back.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(frontTexture)), light, overlay)
    }
}