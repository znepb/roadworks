package me.znepb.roadworks.render

import me.znepb.roadworks.RoadworksClient
import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.container.PostContainer
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RenderUtils
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import org.joml.Vector3d


class PostContainerRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    ContainerRenderer<PostContainerBlockEntity>(ctx) {
    companion object {
        val POST_THIN_EXT_MODEL = ModId("block/post_thin_extension")
        val POST_THIN_FOOTER_MODEL = ModId("block/post_thin_footer")
        val POST_THIN_MID_MODEL = ModId("block/post_thin_midsection")
        val POST_THIN_STUB_MODEL = ModId("block/post_thin_stub")
        val POST_MEDIUM_EXT_MODEL = ModId("block/post_medium_extension")
        val POST_MEDIUM_FOOTER_MODEL = ModId("block/post_medium_footer")
        val POST_MEDIUM_MID_MODEL = ModId("block/post_medium_midsection")
        val POST_MEDIUM_STUB_MODEL = ModId("block/post_medium_stub")
        val POST_THICK_EXT_MODEL = ModId("block/post_thick_extension")
        val POST_THICK_FOOTER_MODEL = ModId("block/post_thick_footer")
        val POST_THICK_MID_MODEL = ModId("block/post_thick_midsection")
        val POST_THICK_STUB_MODEL = ModId("block/post_thick_stub")
    }

    private fun getSidesToRenderForDirection(direction: Direction): List<Direction> {
        val list = mutableListOf<Direction>()
        Direction.entries.forEach {
            if(it != direction && it != direction.opposite) {
                list.add(it)
            }
        }
        return list
    }

    private fun addSideThickness(
        entity: PostContainerBlockEntity,
        state: BlockState,
        direction: Direction,
        connectionSize: PostThickness,
        matrices: MatrixStack,
        buffer: VertexConsumer,
        light: Int,
        overlay: Int,
    ) {
        if(connectionSize == PostThickness.NONE) return

        val size = entity.thickness.id.coerceAtMost(connectionSize.id)

        val sizeModel = when(size) {
            1 -> POST_THIN_EXT_MODEL
            3 -> POST_THICK_EXT_MODEL
            else -> POST_MEDIUM_EXT_MODEL
        }

        val directionsToRender = getSidesToRenderForDirection(Direction.UP)

        matrices.push()
        matrices.multiply(direction.rotationQuaternion, 0.5F, 0.5F, 0.5F)
        RenderUtils.renderModel(matrices, buffer, light, overlay, sizeModel, null, directionsToRender)
        matrices.pop()
    }

    override fun render(
        blockEntity: PostContainerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val buffer: VertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull())
        val midsectionModel = when(blockEntity.thickness) {
            PostThickness.THICK -> POST_THICK_MID_MODEL
            PostThickness.THIN -> POST_THIN_MID_MODEL
            else -> POST_MEDIUM_MID_MODEL
        }

        val interiorSidesToRender = Direction.entries.toMutableList()
        if(blockEntity.up.thickness >= blockEntity.thickness.thickness) interiorSidesToRender.remove(Direction.UP)
        if(blockEntity.down.thickness >= blockEntity.thickness.thickness || blockEntity.footer && !blockEntity.stub) interiorSidesToRender.remove(Direction.DOWN)
        if(blockEntity.north.thickness >= blockEntity.thickness.thickness) interiorSidesToRender.remove(Direction.NORTH)
        if(blockEntity.east.thickness >= blockEntity.thickness.thickness) interiorSidesToRender.remove(Direction.EAST)
        if(blockEntity.south.thickness >= blockEntity.thickness.thickness) interiorSidesToRender.remove(Direction.SOUTH)
        if(blockEntity.west.thickness >= blockEntity.thickness.thickness) interiorSidesToRender.remove(Direction.WEST)

        if(!blockEntity.stub) RenderUtils.renderModel(matrices, buffer, light, overlay, midsectionModel, null, interiorSidesToRender)

        addSideThickness(blockEntity, blockEntity.cachedState, Direction.NORTH, blockEntity.north, matrices, buffer, light, overlay)
        addSideThickness(blockEntity, blockEntity.cachedState, Direction.EAST, blockEntity.east, matrices, buffer, light, overlay)
        addSideThickness(blockEntity, blockEntity.cachedState, Direction.SOUTH, blockEntity.south, matrices, buffer, light, overlay)
        addSideThickness(blockEntity, blockEntity.cachedState, Direction.WEST, blockEntity.west, matrices, buffer, light, overlay)
        addSideThickness(blockEntity, blockEntity.cachedState, Direction.UP, blockEntity.up, matrices, buffer, light, overlay)

        if(blockEntity.footer) {
            // Render footer
            val footerModel = when(blockEntity.thickness) {
                PostThickness.THICK -> POST_THICK_FOOTER_MODEL
                PostThickness.THIN -> POST_THIN_FOOTER_MODEL
                else -> POST_MEDIUM_FOOTER_MODEL
            }

            RenderUtils.renderModel(matrices, buffer, light, overlay, footerModel, null, listOf(Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST))
        } else if(blockEntity.stub) {
            // Render stub
            val stubModel = when(blockEntity.thickness) {
                PostThickness.THICK -> POST_THICK_STUB_MODEL
                PostThickness.THIN -> POST_THIN_STUB_MODEL
                else -> POST_MEDIUM_STUB_MODEL
            }

            RenderUtils.renderModel(matrices, buffer, light, overlay, stubModel, null, listOf(Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST))
        } else {
            addSideThickness(blockEntity, blockEntity.cachedState, Direction.DOWN, blockEntity.down, matrices, buffer, light, overlay)
        }

        this.renderAttachments(blockEntity, tickDelta, matrices, vertexConsumers, light, overlay, Vector3d(0.0, 0.0, blockEntity.thickness.thickness / 2))
    }
}