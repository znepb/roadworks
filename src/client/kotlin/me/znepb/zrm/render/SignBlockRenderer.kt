package me.znepb.zrm.render

import me.znepb.zrm.Registry
import me.znepb.zrm.block.SignBlock
import me.znepb.zrm.block.entity.SignBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import java.util.*

class SignBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<SignBlockEntity> {
    companion object {
        val NORTH_ONLY = EnumSet.of(Direction.NORTH)
        val SOUTH_ONLY = EnumSet.of(Direction.SOUTH)

        val TEXTURE = Identifier("zrm:textures/block/pole.png")

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

    private fun getSignFrontTexture(entity: SignBlockEntity): String? {
        if(entity.cachedState.block is SignBlock) {
            return (entity.cachedState.block as SignBlock).frontTexture
        }

        return null
    }

    private fun getSignBackTexture(entity: SignBlockEntity): String? {
        if(entity.cachedState.block is SignBlock) {
            return (entity.cachedState.block as SignBlock).backTexture
        }

        return null
    }

    private fun addSideThickness(
        blockEntity: SignBlockEntity,
        direction: Direction,
        connectionSize: Int,
        matrices: MatrixStack,
        buffer: VertexConsumer,
        light: Int,
        overlay: Int,
    ) {
        if(connectionSize == 0) return
        if(direction.id == blockEntity.signFacing) return

        val sizeModel = when(connectionSize) {
            1 -> PostBlockRenderer.POST_THIN_EXT_MODEL
            3 -> PostBlockRenderer.POST_THICK_EXT_MODEL
            else -> PostBlockRenderer.POST_MEDIUM_EXT_MODEL
        }

        matrices.push()
        matrices.multiply(direction.rotationQuaternion, 0.5F, 0.5F, 0.5F)
        RenderUtils.renderModel(matrices, buffer, light, overlay, sizeModel, null)
        matrices.pop()
    }

    // TODO: bake models? e.g., generate once then store in a variable until any of the nbt vars are changed

    override fun render(
        blockEntity: SignBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val buffer: VertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull())

        val maxThickness = SignBlockEntity.getThickest(blockEntity)
        val baseCuboids = mutableListOf<ModelPart.Cuboid>()

        val frontTexture = Identifier("zrm", "textures/block/signs/${getSignFrontTexture(blockEntity)}.png")
        val backTexture = Identifier("zrm", "textures/block/signs/${getSignBackTexture(blockEntity)}.png")

        if(!blockEntity.wall) {
            val midsectionModel = when(maxThickness) {
                3 -> PostBlockRenderer.POST_THICK_MID_MODEL
                2 -> PostBlockRenderer.POST_MEDIUM_MID_MODEL
                1 -> PostBlockRenderer.POST_THIN_MID_MODEL
                else -> null
            }

            if(midsectionModel != null) {
                RenderUtils.renderModel(matrices, buffer, light, overlay, midsectionModel, null)
            }

            addSideThickness(blockEntity, Direction.NORTH, blockEntity.north, matrices, buffer, light, overlay)
            addSideThickness(blockEntity, Direction.EAST, blockEntity.east, matrices, buffer, light, overlay)
            addSideThickness(blockEntity, Direction.SOUTH, blockEntity.south, matrices, buffer, light, overlay)
            addSideThickness(blockEntity, Direction.WEST, blockEntity.west, matrices, buffer, light, overlay)
            addSideThickness(blockEntity, Direction.UP, blockEntity.up, matrices, buffer, light, overlay)
            addSideThickness(blockEntity, Direction.DOWN, blockEntity.down, matrices, buffer, light, overlay)
        }

        val signObjectFront = if(blockEntity.wall) WALL_SIGN_FRONT else {
            when (maxThickness) {
                3 -> POST_SIGN_THICK_FRONT
                2 -> POST_SIGN_MEDIUM_FRONT
                1 -> POST_SIGN_THIN_FRONT
                else -> POST_SIGN_NONE_FRONT
            }
        }

        val signObjectBack = if(blockEntity.wall) WALL_SIGN_BACK else {
            when (maxThickness) {
                3 -> POST_SIGN_THICK_BACK
                2 -> POST_SIGN_MEDIUM_BACK
                1 -> POST_SIGN_THIN_BACK
                else -> POST_SIGN_NONE_BACK
            }
        }

        // Render final model
        ModelPart(baseCuboids, Collections.emptyMap()).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(
            TEXTURE
        )), light, overlay)

        val front = ModelPart(mutableListOf(signObjectFront), Collections.emptyMap())
        when(blockEntity.signFacing) {
            Direction.EAST.id -> front.setPivot(16F, 16F, 16F)
            Direction.SOUTH.id -> front.setPivot(0F, 16F, 16F)
            Direction.WEST.id -> front.setPivot(0F, 16F, 0F)
            else -> front.setPivot(16F, 16F, 0F)
        }
        front.setAngles(
            0F,
            when(blockEntity.signFacing) {
                Direction.NORTH.id -> 0F
                Direction.EAST.id -> Math.toRadians(90.0).toFloat()
                Direction.SOUTH.id -> Math.toRadians(180.0).toFloat()
                Direction.WEST.id -> Math.toRadians(270.0).toFloat()
                else -> 0F
            },
            Math.toRadians(180.0).toFloat()
        )
        front.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(frontTexture)), light, overlay)

        val back = ModelPart(mutableListOf(signObjectBack), Collections.emptyMap())
        back.copyTransform(front)
        back.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(backTexture)), light, overlay)
    }
}