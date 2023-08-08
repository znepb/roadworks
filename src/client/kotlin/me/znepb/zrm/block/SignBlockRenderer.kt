package me.znepb.zrm.block

import me.znepb.zrm.Main.logger
import me.znepb.zrm.Registry
import me.znepb.zrm.block.PostBlockRenderer
import net.minecraft.block.BlockState
import net.minecraft.client.model.Model
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import org.joml.Vector3f
import java.util.*
import kotlin.math.sign


class SignBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<SignBlockEntity> {
    companion object {
        val ALL_DIRECTIONS: Set<Direction> = EnumSet.allOf(Direction::class.java)
        val NORTH_ONLY = EnumSet.of(Direction.NORTH)
        val EAST_ONLY = EnumSet.of(Direction.EAST)
        val SOUTH_ONLY = EnumSet.of(Direction.SOUTH)
        val WEST_ONLY = EnumSet.of(Direction.WEST)

        val TEXTURE = Identifier("zrm:textures/block/pole.png")
        val SIGN_FRONT = Identifier("zrm:textures/block/stop_sign.png")
        val SIGN_BACK = Identifier("zrm:textures/block/back_octagon.png")

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

        val EMPTY_CUBOID = ModelPart.Cuboid(0, 0, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, false, 0F, 0F, ALL_DIRECTIONS);
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

    private fun addSideThickness(direction: Direction, connectionSize: Int, list: MutableList<ModelPart.Cuboid>) {
        if(connectionSize == 0) return

        when(connectionSize) {
            1 -> {
                list.add(when(direction) {
                    Direction.EAST -> PostBlockRenderer.EAST_THIN
                    Direction.SOUTH -> PostBlockRenderer.SOUTH_THIN
                    Direction.WEST -> PostBlockRenderer.WEST_THIN
                    Direction.UP -> PostBlockRenderer.UP_THIN
                    Direction.DOWN -> PostBlockRenderer.DOWN_THIN
                    else -> PostBlockRenderer.NORTH_THIN
                })
            }
            2 -> {
                list.add(when(direction) {
                    Direction.EAST -> PostBlockRenderer.EAST_MEDIUM
                    Direction.SOUTH -> PostBlockRenderer.SOUTH_MEDIUM
                    Direction.WEST -> PostBlockRenderer.WEST_MEDIUM
                    Direction.UP -> PostBlockRenderer.UP_MEDIUM
                    Direction.DOWN -> PostBlockRenderer.DOWN_MEDIUM
                    else -> PostBlockRenderer.NORTH_MEDIUM
                })
            }
            3 -> {
                list.add(when(direction) {
                    Direction.EAST -> PostBlockRenderer.EAST_THICK
                    Direction.SOUTH -> PostBlockRenderer.SOUTH_THICK
                    Direction.WEST -> PostBlockRenderer.WEST_THICK
                    Direction.UP -> PostBlockRenderer.UP_THICK
                    Direction.DOWN -> PostBlockRenderer.DOWN_THICK
                    else -> PostBlockRenderer.NORTH_THICK
                })
            }
        }
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
        val maxThickness = blockEntity.up
            .coerceAtLeast(blockEntity.down)
            .coerceAtLeast(blockEntity.north)
            .coerceAtLeast(blockEntity.east)
            .coerceAtLeast(blockEntity.south)
            .coerceAtLeast(blockEntity.west)

        val baseCuboids = mutableListOf<ModelPart.Cuboid>()

        val frontTexture = Identifier("zrm", "textures/block/${getSignFrontTexture(blockEntity)}.png")
        val backTexture = Identifier("zrm", "textures/block/${getSignBackTexture(blockEntity)}.png")

        if(!blockEntity.wall) {
            val center = when(maxThickness) {
                3 -> PostBlockRenderer.CENTER_THICK
                2 -> PostBlockRenderer.CENTER_MEDIUM
                1 -> PostBlockRenderer.CENTER_THIN
                else -> null
            }

            if(center != null) baseCuboids.add(center)

            addSideThickness(Direction.NORTH, blockEntity.north, baseCuboids)
            addSideThickness(Direction.EAST, blockEntity.east, baseCuboids)
            addSideThickness(Direction.SOUTH, blockEntity.south, baseCuboids)
            addSideThickness(Direction.WEST, blockEntity.west, baseCuboids)
            addSideThickness(Direction.UP, blockEntity.up, baseCuboids)
            addSideThickness(Direction.DOWN, blockEntity.down, baseCuboids)
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
        ModelPart(baseCuboids, Collections.emptyMap()).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE)), light, overlay)

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