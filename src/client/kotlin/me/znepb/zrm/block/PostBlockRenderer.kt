package me.znepb.zrm.block

import me.znepb.zrm.Registry
import net.minecraft.block.BlockState
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import java.util.*

class PostBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<PostBlockEntity> {
    companion object {
        val ALL_DIRECTIONS: Set<Direction> = EnumSet.allOf(Direction::class.java)
        val FOOTER_DIRECTIONS = EnumSet.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP)
        val VERT_DIRECTIONS = EnumSet.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
        val NS_DIRECTIONS = EnumSet.of(Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN)
        val EW_DIRECTIONS = EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN)

        val TEXTURE = Identifier("zrm:textures/block/pole.png")
        val FOOTER = Identifier("zrm:textures/block/concrete.png")

        val FOOTER_THICK = ModelPart.Cuboid(0, 0, 3.0F, 0.0F, 3.0F, 10.0F, 3.0F, 10.0F, 0F, 0F, 0F, false, 16F, 16F, FOOTER_DIRECTIONS)
        val CENTER_THICK = ModelPart.Cuboid(0, 0, 5.0F, 5.0F, 5.0F, 6.0F, 6.0F, 6.0F, 0F, 0F, 0F, false, 16F, 16F, ALL_DIRECTIONS)
        val DOWN_THICK = ModelPart.Cuboid(0, 0, 5.0F, 0.0F, 5.0F, 6.0F, 5.0F, 6.0F, 0F, 0F, 0F, false, 16F, 16F, VERT_DIRECTIONS)
        val DOWN_FOOTER_THICK = ModelPart.Cuboid(0, 0, 5.0F, 3.0F, 5.0F, 6.0F, 2.0F, 6.0F, 0F, 0F, 0F, false, 16F, 16F, VERT_DIRECTIONS)
        val UP_THICK = ModelPart.Cuboid(0, 0, 5.0F, 11.0F, 5.0F, 6.0F, 5.0F, 6.0F, 0F, 0F, 0F, false, 16F, 16F, VERT_DIRECTIONS)
        val NORTH_THICK = ModelPart.Cuboid(0, 0, 5.0F, 5.0F, 0.0F, 6.0F, 6.0F, 5.0F, 0F, 0F, 0F, false, 16F, 16F, NS_DIRECTIONS)
        val SOUTH_THICK = ModelPart.Cuboid(0, 0, 5.0F, 5.0F, 11.0F, 6.0F, 6.0F, 5.0F, 0F, 0F, 0F, false, 16F, 16F, NS_DIRECTIONS)
        val EAST_THICK = ModelPart.Cuboid(0, 0, 11.0F, 5.0F, 5.0F, 5.0F, 6.0F, 6.0F, 0F, 0F, 0F, false, 16F, 16F, EW_DIRECTIONS)
        val WEST_THICK = ModelPart.Cuboid(0, 0, 0.0F, 5.0F, 5.0F, 5.0F, 6.0F, 6.0F, 0F, 0F, 0F, false, 16F, 16F, EW_DIRECTIONS)

        val FOOTER_MEDIUM = ModelPart.Cuboid(0, 0, 5.0F, 0.0F, 5.0F, 6.0F, 2.0F, 6.0F, 0F, 0F, 0F, false, 16F, 16F, FOOTER_DIRECTIONS)
        val CENTER_MEDIUM = ModelPart.Cuboid(0, 0, 6.0F, 6.0F, 6.0F, 4.0F, 4.0F, 4.0F, 0F, 0F, 0F, false, 16F, 16F, ALL_DIRECTIONS)
        val DOWN_MEDIUM = ModelPart.Cuboid(0, 0, 6.0F, 0.0F, 6.0F, 4.0F, 6.0F, 4.0F, 0F, 0F, 0F, false, 16F, 16F, VERT_DIRECTIONS)
        val DOWN_FOOTER_MEDIUM = ModelPart.Cuboid(0, 0, 6.0F, 2.0F, 6.0F, 4.0F, 4.0F, 4.0F, 0F, 0F, 0F, false, 16F, 16F, VERT_DIRECTIONS)
        val UP_MEDIUM = ModelPart.Cuboid(0, 0, 6.0F, 10.0F, 6.0F, 4.0F, 6.0F, 4.0F, 0F, 0F, 0F, false, 16F, 16F, VERT_DIRECTIONS)
        val NORTH_MEDIUM = ModelPart.Cuboid(0, 0, 6.0F, 6.0F, 0.0F, 4.0F, 4.0F, 6.0F, 0F, 0F, 0F, false, 16F, 16F, NS_DIRECTIONS)
        val SOUTH_MEDIUM = ModelPart.Cuboid(0, 0, 6.0F, 6.0F, 10.0F, 4.0F, 4.0F, 6.0F, 0F, 0F, 0F, false, 16F, 16F, NS_DIRECTIONS)
        val EAST_MEDIUM = ModelPart.Cuboid(0, 0, 10.0F, 6.0F, 6.0F, 6.0F, 4.0F, 4.0F, 0F, 0F, 0F, false, 16F, 16F, EW_DIRECTIONS)
        val WEST_MEDIUM = ModelPart.Cuboid(0, 0, 0.0F, 6.0F, 6.0F, 6.0F, 4.0F, 4.0F, 0F, 0F, 0F, false, 16F, 16F, EW_DIRECTIONS)

        val FOOTER_THIN = ModelPart.Cuboid(0, 0, 6.0F, 0.0F, 6.0F, 4.0F, 1.0F, 4.0F, 0F, 0F, 0F, false, 16F, 16F, FOOTER_DIRECTIONS)
        val CENTER_THIN = ModelPart.Cuboid(0, 0, 7.0F, 7.0F, 7.0F, 2.0F, 2.0F, 2.0F, 0F, 0F, 0F, false, 16F, 16F, ALL_DIRECTIONS)
        val DOWN_THIN = ModelPart.Cuboid(0, 0, 7.0F, 0.0F, 7.0F, 2.0F, 7.0F, 2.0F, 0F, 0F, 0F, false, 16F, 16F, VERT_DIRECTIONS)
        val DOWN_FOOTER_THIN = ModelPart.Cuboid(0, 0, 7.0F, 1.0F, 7.0F, 2.0F, 6.0F, 2.0F, 0F, 0F, 0F, false, 16F, 16F, VERT_DIRECTIONS)
        val UP_THIN = ModelPart.Cuboid(0, 0, 7.0F, 9.0F, 7.0F, 2.0F, 7.0F, 2.0F, 0F, 0F, 0F, false, 16F, 16F, VERT_DIRECTIONS)
        val NORTH_THIN = ModelPart.Cuboid(0, 0, 7.0F, 7.0F, 0.0F, 2.0F, 2.0F, 7.0F, 0F, 0F, 0F, false, 16F, 16F, NS_DIRECTIONS)
        val SOUTH_THIN = ModelPart.Cuboid(0, 0, 7.0F, 7.0F, 9.0F, 2.0F, 2.0F, 7.0F, 0F, 0F, 0F, false, 16F, 16F, NS_DIRECTIONS)
        val EAST_THIN = ModelPart.Cuboid(0, 0, 9.0F, 7.0F, 7.0F, 7.0F, 2.0F, 2.0F, 0F, 0F, 0F, false, 16F, 16F, EW_DIRECTIONS)
        val WEST_THIN = ModelPart.Cuboid(0, 0, 0.0F, 7.0F, 7.0F, 7.0F, 2.0F, 2.0F, 0F, 0F, 0F, false, 16F, 16F, EW_DIRECTIONS)
    }

    private fun addSideThickness(state: BlockState, direction: Direction, connectionSize: Int, list: MutableList<ModelPart.Cuboid>) {
        if(connectionSize == 0) return

        val size = when(state.block) {
            Registry.ModBlocks.THIN_POST -> 1
            Registry.ModBlocks.POST -> 2
            Registry.ModBlocks.THICK_POST -> 3
            else -> 0
        }.coerceAtMost(connectionSize)

        when(size) {
            1 -> {
                list.add(when(direction) {
                    Direction.EAST -> EAST_THIN
                    Direction.SOUTH -> SOUTH_THIN
                    Direction.WEST -> WEST_THIN
                    Direction.UP -> UP_THIN
                    Direction.DOWN -> DOWN_THIN
                    else -> NORTH_THIN
                })
            }
            2 -> {
                list.add(when(direction) {
                    Direction.EAST -> EAST_MEDIUM
                    Direction.SOUTH -> SOUTH_MEDIUM
                    Direction.WEST -> WEST_MEDIUM
                    Direction.UP -> UP_MEDIUM
                    Direction.DOWN -> DOWN_MEDIUM
                    else -> NORTH_MEDIUM
                })
            }
            3 -> {
                list.add(when(direction) {
                    Direction.EAST -> EAST_THICK
                    Direction.SOUTH -> SOUTH_THICK
                    Direction.WEST -> WEST_THICK
                    Direction.UP -> UP_THICK
                    Direction.DOWN -> DOWN_THICK
                    else -> NORTH_THICK
                })
            }
            else -> {}
        }
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
        val baseCuboids = mutableListOf(when(blockEntity.cachedState.block) {
            Registry.ModBlocks.THICK_POST -> CENTER_THICK
            Registry.ModBlocks.THIN_POST -> CENTER_THIN
            else -> CENTER_MEDIUM
        })

        addSideThickness(blockEntity.cachedState, Direction.NORTH, blockEntity.north, baseCuboids)
        addSideThickness(blockEntity.cachedState, Direction.EAST, blockEntity.east, baseCuboids)
        addSideThickness(blockEntity.cachedState, Direction.SOUTH, blockEntity.south, baseCuboids)
        addSideThickness(blockEntity.cachedState, Direction.WEST, blockEntity.west, baseCuboids)
        addSideThickness(blockEntity.cachedState, Direction.UP, blockEntity.up, baseCuboids)

        if(blockEntity.footer) {
            // Render footer
            val baseCuboidsFooter = arrayOf(when(blockEntity.cachedState.block) {
                Registry.ModBlocks.THICK_POST -> FOOTER_THICK
                Registry.ModBlocks.THIN_POST -> FOOTER_THIN
                else -> FOOTER_MEDIUM
            }).toList()

            ModelPart(baseCuboidsFooter, Collections.emptyMap()).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(FOOTER)), light, overlay)

            // Add support coming out of footer
            baseCuboids.add(when(blockEntity.cachedState.block) {
                Registry.ModBlocks.THICK_POST -> DOWN_FOOTER_THICK
                Registry.ModBlocks.THIN_POST -> DOWN_FOOTER_THIN
                else -> DOWN_FOOTER_MEDIUM
            })
        } else {
            addSideThickness(blockEntity.cachedState, Direction.DOWN, blockEntity.down, baseCuboids)
        }

        // Render final model
        ModelPart(baseCuboids, Collections.emptyMap()).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE)), light, overlay)
    }
}