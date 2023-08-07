package me.znepb.zrm.datagen

import me.znepb.zrm.Registry
import me.znepb.zrm.block.SignBlock
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.data.client.*
import net.minecraft.data.client.BlockStateModelGenerator.createSingletonBlockState
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import java.util.*
import kotlin.collections.HashMap


class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(generator: BlockStateModelGenerator) {
        addSignOrPole(generator, Registry.ModBlocks.POST, "post")
        addSignOrPole(generator, Registry.ModBlocks.THICK_POST, "thick_post")
        addSignOrPole(generator, Registry.ModBlocks.THIN_POST, "thin_post")

        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.TRAFFIC_CONE, Identifier("zrm", "block/traffic_cone")))
    }

    fun addSignOrPole(generator: BlockStateModelGenerator, block: Block, name: String) {
        generator.blockStateCollector.accept(createSingletonBlockState(block, Identifier("zrm", "block/$name")))
    }

    override fun generateItemModels(generator: ItemModelGenerator) {
    }

}