package me.znepb.zrm.datagen

import me.znepb.zrm.Main.ModId
import me.znepb.zrm.Registry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class TagProvider(output: FabricDataOutput, completableFuture: CompletableFuture<WrapperLookup>) : BlockTagProvider(output, completableFuture) {
    companion object {
        val POSTS = TagKey.of(RegistryKeys.BLOCK, ModId("posts"))
        val POST_MOUNTABLES = TagKey.of(RegistryKeys.BLOCK, ModId("post_mountables"))

        val SIGNS = listOf(
            Registry.ModBlocks.STOP_SIGN,
            Registry.ModBlocks.STOP_SIGN_4_WAY,
            Registry.ModBlocks.STOP_SIGN_AHEAD,
            Registry.ModBlocks.YIELD_SIGN,
            Registry.ModBlocks.YIELD_SIGN_AHEAD,
            Registry.ModBlocks.SIGNAL_AHEAD,
            Registry.ModBlocks.ROAD_WORK_AHEAD
        )
    }

    override fun configure(arg: WrapperLookup) {
        getOrCreateTagBuilder(POSTS)
            .add(Registry.ModBlocks.POST)
            .add(Registry.ModBlocks.THIN_POST)
            .add(Registry.ModBlocks.THICK_POST)

        with(getOrCreateTagBuilder(POST_MOUNTABLES)) {
            SIGNS.forEach { this.add(it) }
        }
        getOrCreateTagBuilder(POST_MOUNTABLES).add(Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL)
        getOrCreateTagBuilder(POST_MOUNTABLES).add(Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT)
        getOrCreateTagBuilder(POST_MOUNTABLES).add(Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT)
        getOrCreateTagBuilder(POST_MOUNTABLES).add(Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT)
    }
}

