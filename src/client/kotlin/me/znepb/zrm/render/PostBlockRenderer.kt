package me.znepb.zrm.render

import me.znepb.zrm.Registry
import me.znepb.zrm.block.entity.PostBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import java.util.*

class PostBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<PostBlockEntity> {
    companion object {
        val POST_THIN_EXT_MODEL = Identifier("zrm", "block/post_thin_extension")
        val POST_THIN_FOOTER_MODEL = Identifier("zrm", "block/post_thin_footer")
        val POST_THIN_MID_MODEL = Identifier("zrm", "block/post_thin_midsection")
        val POST_MEDIUM_EXT_MODEL = Identifier("zrm", "block/post_medium_extension")
        val POST_MEDIUM_FOOTER_MODEL = Identifier("zrm", "block/post_medium_footer")
        val POST_MEDIUM_MID_MODEL = Identifier("zrm", "block/post_medium_midsection")
        val POST_THICK_EXT_MODEL = Identifier("zrm", "block/post_thick_extension")
        val POST_THICK_FOOTER_MODEL = Identifier("zrm", "block/post_thick_footer")
        val POST_THICK_MID_MODEL = Identifier("zrm", "block/post_thick_midsection")
    }

    private fun addSideThickness(
        state: BlockState,
        direction: Direction,
        connectionSize: Int,
        matrices: MatrixStack,
        buffer: VertexConsumer,
        light: Int,
        overlay: Int,
    ) {
        if(connectionSize == 0) return

        val size = when(state.block) {
            Registry.ModBlocks.THIN_POST -> 1
            Registry.ModBlocks.POST -> 2
            Registry.ModBlocks.THICK_POST -> 3
            else -> 0
        }.coerceAtMost(connectionSize)

        val sizeModel = when(size) {
            1 -> POST_THIN_EXT_MODEL
            3 -> POST_THICK_EXT_MODEL
            else -> POST_MEDIUM_EXT_MODEL
        }

        matrices.push()
        matrices.multiply(direction.rotationQuaternion, 0.5F, 0.5F, 0.5F)
        RenderUtils.renderModel(matrices, buffer, light, overlay, sizeModel, null)
        matrices.pop()
    }

    // TODO: bake models? e.g., generate once then store in a variable until any of the nbt vars are changed

    override fun render(
        blockEntity: PostBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val buffer: VertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull())
        val midsectionModel = when(blockEntity.cachedState.block) {
            Registry.ModBlocks.THICK_POST -> POST_THICK_MID_MODEL
            Registry.ModBlocks.THIN_POST -> POST_THIN_MID_MODEL
            else -> POST_MEDIUM_MID_MODEL
        }

        RenderUtils.renderModel(matrices, buffer, light, overlay, midsectionModel, null)

        addSideThickness(blockEntity.cachedState, Direction.NORTH, blockEntity.north, matrices, buffer, light, overlay)
        addSideThickness(blockEntity.cachedState, Direction.EAST, blockEntity.east, matrices, buffer, light, overlay)
        addSideThickness(blockEntity.cachedState, Direction.SOUTH, blockEntity.south, matrices, buffer, light, overlay)
        addSideThickness(blockEntity.cachedState, Direction.WEST, blockEntity.west, matrices, buffer, light, overlay)
        addSideThickness(blockEntity.cachedState, Direction.UP, blockEntity.up, matrices, buffer, light, overlay)

        if(blockEntity.footer) {
            // Render footer
            val footerModel = when(blockEntity.cachedState.block) {
                Registry.ModBlocks.THICK_POST -> POST_THICK_FOOTER_MODEL
                Registry.ModBlocks.THIN_POST -> POST_THIN_FOOTER_MODEL
                else -> POST_MEDIUM_FOOTER_MODEL
            }

            RenderUtils.renderModel(matrices, buffer, light, overlay, footerModel, null)
        } else {
            addSideThickness(blockEntity.cachedState, Direction.DOWN, blockEntity.down, matrices, buffer, light, overlay)
        }
    }
}