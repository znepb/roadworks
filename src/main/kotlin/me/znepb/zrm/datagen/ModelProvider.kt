package me.znepb.zrm.datagen

import me.znepb.zrm.Registry
import me.znepb.zrm.block.SignBlock
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.data.client.*
import net.minecraft.data.client.BlockStateModelGenerator.createSingletonBlockState
import net.minecraft.util.Identifier
import java.util.*

class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    companion object {
        val signModel = Model(
            Optional.of(Identifier("zrm", "block/sign_base")), Optional.empty(),
            TextureKey.FRONT,
            TextureKey.BACK
        )
    }

    override fun generateBlockStateModels(generator: BlockStateModelGenerator) {
        addPole(generator, Registry.ModBlocks.POST, "post")
        addPole(generator, Registry.ModBlocks.THICK_POST, "thick_post")
        addPole(generator, Registry.ModBlocks.THIN_POST, "thin_post")

        addSign(generator, Registry.ModBlocks.STOP_SIGN, "stop_sign")
        addSign(generator, Registry.ModBlocks.STOP_SIGN_4_WAY, "stop_sign_4_way")
        addSign(generator, Registry.ModBlocks.STOP_SIGN_AHEAD, "stop_ahead_sign")
        addSign(generator, Registry.ModBlocks.YIELD_SIGN, "yield_sign")
        addSign(generator, Registry.ModBlocks.YIELD_SIGN_AHEAD, "yield_ahead_sign")
        addSign(generator, Registry.ModBlocks.SIGNAL_AHEAD, "signal_ahead")

        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.TRAFFIC_CONE, Identifier("zrm", "block/traffic_cone")))
    }

    fun addPole(generator: BlockStateModelGenerator, block: Block, name: String) {
        generator.blockStateCollector.accept(createSingletonBlockState(block, Identifier("zrm", "block/$name")))
    }

    fun addSign(generator: BlockStateModelGenerator, block: SignBlock, name: String) {
        generator.blockStateCollector.accept(createSingletonBlockState(block, Identifier("zrm", "block/$name")))

        signModel.upload(
            block,
            TextureMap()
                .put(TextureKey.FRONT, Identifier("zrm", "block/${block.frontTexture}"))
                .put(TextureKey.BACK, Identifier("zrm","block/${block.backTexture}")),
            generator.modelCollector
        )
    }



    override fun generateItemModels(generator: ItemModelGenerator) {
    }

}