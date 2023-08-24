package me.znepb.roadworks.datagen

import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.Registry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents.TagsLoaded
import net.minecraft.block.Blocks
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class TagProvider(output: FabricDataOutput, completableFuture: CompletableFuture<WrapperLookup>) : BlockTagProvider(output, completableFuture) {
    companion object {
        val POSTS = TagKey.of(RegistryKeys.BLOCK, ModId("posts"))
        val POST_MOUNTABLES = TagKey.of(RegistryKeys.BLOCK, ModId("post_mountables"))
        val MARKINGS = TagKey.of(RegistryKeys.BLOCK, ModId("marking"))

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
        getOrCreateTagBuilder(MARKINGS)
            .add(Registry.ModBlocks.WHITE_INFILL_MARKING)
            .add(Registry.ModBlocks.WHITE_CENTER_DASH_MARKING)
            .add(Registry.ModBlocks.WHITE_CENTER_TURN_MARKING)
            .add(Registry.ModBlocks.WHITE_CENTER_MARKING)
            .add(Registry.ModBlocks.WHITE_CENTER_THICK)
            .add(Registry.ModBlocks.WHITE_CENTER_STUB_SHORT)
            .add(Registry.ModBlocks.WHITE_CENTER_STUB_MEDIUM)
            .add(Registry.ModBlocks.WHITE_CENTER_STUB_LONG)
            .add(Registry.ModBlocks.WHITE_EDGE_DASH_MARKING)
            .add(Registry.ModBlocks.WHITE_EDGE_MARKING)
            .add(Registry.ModBlocks.WHITE_EDGE_TURN_MARKING_INSIDE)
            .add(Registry.ModBlocks.WHITE_EDGE_TURN_MARKING_OUTSIDE)
            .add(Registry.ModBlocks.WHITE_EDGE_THICK)
            .add(Registry.ModBlocks.WHITE_EDGE_STUB_SHORT_LEFT)
            .add(Registry.ModBlocks.WHITE_EDGE_STUB_MEDIUM_LEFT)
            .add(Registry.ModBlocks.WHITE_EDGE_STUB_LONG_LEFT)
            .add(Registry.ModBlocks.WHITE_EDGE_STUB_SHORT_RIGHT)
            .add(Registry.ModBlocks.WHITE_EDGE_STUB_MEDIUM_RIGHT)
            .add(Registry.ModBlocks.WHITE_EDGE_STUB_LONG_RIGHT)
            .add(Registry.ModBlocks.WHITE_T_CENTER_LONG)
            .add(Registry.ModBlocks.WHITE_T_CENTER)
            .add(Registry.ModBlocks.WHITE_T_CENTER_LEFT)
            .add(Registry.ModBlocks.WHITE_T_CENTER_RIGHT)
            .add(Registry.ModBlocks.WHITE_T_CENTER_SHORT)
            .add(Registry.ModBlocks.WHITE_T_SHORT_LEFT)
            .add(Registry.ModBlocks.WHITE_T_SHORT_RIGHT)
            .add(Registry.ModBlocks.WHITE_L_THIN_LEFT)
            .add(Registry.ModBlocks.WHITE_L_THIN_RIGHT)
            .add(Registry.ModBlocks.WHITE_L_LEFT)
            .add(Registry.ModBlocks.WHITE_L_RIGHT)
            .add(Registry.ModBlocks.WHITE_L_SHORT_LEFT)
            .add(Registry.ModBlocks.WHITE_L_SHORT_RIGHT)

        getOrCreateTagBuilder(POSTS)
            .add(Registry.ModBlocks.POST)
            .add(Registry.ModBlocks.THIN_POST)
            .add(Registry.ModBlocks.THICK_POST)

        with(getOrCreateTagBuilder(POST_MOUNTABLES)) {
            SIGNS.forEach { this.add(it) }
        }

        getOrCreateTagBuilder(POST_MOUNTABLES).add(
            Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL,
            Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT,
            Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT,
            Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT,
            Registry.ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT,
            Registry.ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT
        )
    }
}

