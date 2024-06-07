package me.znepb.roadworks.datagen

import me.znepb.roadworks.Registry
import me.znepb.roadworks.RoadworksMain.ModId
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.tag.TagKey
import java.util.concurrent.CompletableFuture

class TagProvider(output: FabricDataOutput, completableFuture: CompletableFuture<WrapperLookup>) : BlockTagProvider(output, completableFuture) {
    companion object {
        val POSTS = TagKey.of(RegistryKeys.BLOCK, ModId("posts"))
        val POST_MOUNTABLES = TagKey.of(RegistryKeys.BLOCK, ModId("post_mountables"))
        val MARKINGS = TagKey.of(RegistryKeys.BLOCK, ModId("marking"))
        val STANDALONE_MARKINGS = TagKey.of(RegistryKeys.BLOCK, ModId("standalone_markings"))

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
        with(Registry.ModBlocks) {
            getOrCreateTagBuilder(STANDALONE_MARKINGS).add(
                WHITE_ARROW_LEFT_MARKING,
                WHITE_ARROW_STRAIGHT_MARKING,
                WHITE_ARROW_RIGHT_MARKING,
                WHITE_ONLY_MARKING,
                WHITE_HOV_MARKING,
                WHITE_RAILROAD_MARKING,
                WHITE_ARROW_LEFT_STRAIGHT_MARKING,
                WHITE_ARROW_RIGHT_STRAIGHT_MARKING,
                WHITE_ARROW_RIGHT_LEFT_MARKING,
                WHITE_ARROW_U_TURN_MARKING,
                WHITE_ZEBRA_CROSSING_MARKING
            )

            getOrCreateTagBuilder(MARKINGS).add(
                WHITE_INFILL_MARKING,
                WHITE_ARROW_LEFT_MARKING,
                WHITE_ARROW_STRAIGHT_MARKING,
                WHITE_ARROW_RIGHT_MARKING,
                WHITE_ONLY_MARKING,
                WHITE_HOV_MARKING,
                WHITE_RAILROAD_MARKING,
                WHITE_ARROW_LEFT_STRAIGHT_MARKING,
                WHITE_ARROW_RIGHT_STRAIGHT_MARKING,
                WHITE_ARROW_RIGHT_LEFT_MARKING,
                WHITE_ARROW_U_TURN_MARKING,
                WHITE_ZEBRA_CROSSING_MARKING,
                WHITE_CENTER_DASH_MARKING,
                WHITE_CENTER_TURN_MARKING,
                WHITE_CENTER_MARKING,
                WHITE_CENTER_THICK,
                WHITE_CENTER_STUB_SHORT,
                WHITE_CENTER_STUB_MEDIUM,
                WHITE_CENTER_STUB_LONG,
                WHITE_EDGE_DASH_MARKING,
                WHITE_EDGE_MARKING,
                WHITE_EDGE_TURN_MARKING_INSIDE,
                WHITE_EDGE_TURN_MARKING_OUTSIDE,
                WHITE_EDGE_THICK,
                WHITE_EDGE_STUB_SHORT_LEFT,
                WHITE_EDGE_STUB_MEDIUM_LEFT,
                WHITE_EDGE_STUB_LONG_LEFT,
                WHITE_EDGE_STUB_SHORT_RIGHT,
                WHITE_EDGE_STUB_MEDIUM_RIGHT,
                WHITE_EDGE_STUB_LONG_RIGHT,
                WHITE_T_CENTER_LONG,
                WHITE_T_LEFT_LONG,
                WHITE_T_RIGHT_LONG,
                WHITE_T_CENTER,
                WHITE_T_CENTER_LEFT,
                WHITE_T_CENTER_RIGHT,
                WHITE_T_CENTER_SHORT,
                WHITE_T_SHORT_LEFT,
                WHITE_T_SHORT_RIGHT,
                WHITE_L_THIN_LEFT,
                WHITE_L_THIN_RIGHT,
                WHITE_L_LEFT,
                WHITE_L_RIGHT,
                WHITE_L_SHORT_LEFT,
                WHITE_L_SHORT_RIGHT,
                YELLOW_INFILL_MARKING,
                YELLOW_CENTER_DASH_MARKING,
                YELLOW_CENTER_TURN_MARKING,
                YELLOW_CENTER_MARKING,
                YELLOW_CENTER_OFFSET,
                YELLOW_DOUBLE,
                YELLOW_DOUBLE_TURN,
                YELLOW_DOUBLE_SPLIT_LEFT,
                YELLOW_DOUBLE_SPLIT_RIGHT,
                YELLOW_CENTER_OFFSET_INSIDE,
                YELLOW_CENTER_OFFSET_OUTSIDE,
                YELLOW_OFFSET_OUTSIDE_TO_CENTER_R,
                YELLOW_OFFSET_OUTSIDE_TO_CENTER_L,
                YELLOW_OFFSET_INSIDE_TO_CENTER_R,
                YELLOW_OFFSET_INSIDE_TO_CENTER_L,
                YELLOW_CENTER_STUB_SHORT,
                YELLOW_CENTER_STUB_MEDIUM,
                YELLOW_CENTER_STUB_LONG,
                YELLOW_EDGE_DASH_MARKING,
                YELLOW_EDGE_MARKING,
                YELLOW_EDGE_TURN_MARKING_INSIDE,
                YELLOW_EDGE_TURN_MARKING_OUTSIDE,
                YELLOW_EDGE_STUB_SHORT_LEFT,
                YELLOW_EDGE_STUB_MEDIUM_LEFT,
                YELLOW_EDGE_STUB_LONG_LEFT,
                YELLOW_EDGE_STUB_SHORT_RIGHT,
                YELLOW_EDGE_STUB_MEDIUM_RIGHT,
                YELLOW_EDGE_STUB_LONG_RIGHT,
                YELLOW_T_CENTER_LONG,
                YELLOW_T_LEFT_LONG,
                YELLOW_T_RIGHT_LONG,
                YELLOW_T_CENTER,
                YELLOW_T_CENTER_LEFT,
                YELLOW_T_CENTER_RIGHT,
                YELLOW_T_CENTER_SHORT,
                YELLOW_T_SHORT_LEFT,
                YELLOW_T_SHORT_RIGHT,
                YELLOW_L_THIN_LEFT,
                YELLOW_L_THIN_RIGHT,
                YELLOW_L_LEFT,
                YELLOW_L_RIGHT,
                YELLOW_L_SHORT_LEFT,
                YELLOW_L_SHORT_RIGHT
            )

            getOrCreateTagBuilder(POSTS)
                .add(POST, THIN_POST, THICK_POST)

            with(getOrCreateTagBuilder(POST_MOUNTABLES)) {
                SIGNS.forEach { this.add(it) }
            }

            getOrCreateTagBuilder(POST_MOUNTABLES).add(
                ONE_HEAD_GREEN_TRAFFIC_SIGNAL,
                ONE_HEAD_RED_TRAFFIC_SIGNAL,
                ONE_HEAD_YELLOW_TRAFFIC_SIGNAL,
                THREE_HEAD_TRAFFIC_SIGNAL,
                THREE_HEAD_TRAFFIC_SIGNAL_RIGHT,
                THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT,
                THREE_HEAD_TRAFFIC_SIGNAL_LEFT,
                FIVE_HEAD_TRAFFIC_SIGNAL_LEFT,
                FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT,
                PEDESTRIAN_SIGNAL,
                PEDESTRIAN_BUTTON
            )
        }
    }
}

