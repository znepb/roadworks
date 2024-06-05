package me.znepb.roadworks

import me.znepb.roadworks.RoadworksMain.NAMESPACE
import me.znepb.roadworks.init.ModelLoader
import me.znepb.roadworks.render.*
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import org.slf4j.LoggerFactory

object RoadworksClient : ClientModInitializer {
	val logger = LoggerFactory.getLogger(NAMESPACE)

	override fun onInitializeClient() {
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.POST_BLOCK_ENTITY, ::PostBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, ::SignBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.ONE_HEAD_TRAFFIC_SIGNAL_RED_BLOCK_ENTITY, ::RedBeaconRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.ONE_HEAD_TRAFFIC_SIGNAL_YELLOW_BLOCK_ENTITY, ::YellowBeaconRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.ONE_HEAD_TRAFFIC_SIGNAL_GREEN_BLOCK_ENTITY, ::GreenBeaconRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY, ::ThreeHeadTrafficSignalBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_LEFT_BLOCK_ENTITY, ::ThreeHeadTrafficSignalLeftBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT_BLOCK_ENTITY, ::ThreeHeadTrafficSignalStraightBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT_BLOCK_ENTITY, ::ThreeHeadTrafficSignalRightBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT_BLOCK_ENTITY, ::FiveHeadTrafficSignalLeftBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT_BLOCK_ENTITY, ::FiveHeadTrafficSignalRightBlockRenderer)

		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
			Registry.ModBlocks.WHITE_CENTER_MARKING,
			Registry.ModBlocks.WHITE_ARROW_LEFT_MARKING,
			Registry.ModBlocks.WHITE_ARROW_STRAIGHT_MARKING,
			Registry.ModBlocks.WHITE_ARROW_RIGHT_MARKING,
			Registry.ModBlocks.WHITE_ONLY_MARKING,
			Registry.ModBlocks.WHITE_HOV_MARKING,
			Registry.ModBlocks.WHITE_RAILROAD_MARKING,
			Registry.ModBlocks.WHITE_ARROW_LEFT_STRAIGHT_MARKING,
			Registry.ModBlocks.WHITE_ARROW_RIGHT_STRAIGHT_MARKING,
			Registry.ModBlocks.WHITE_ARROW_RIGHT_LEFT_MARKING,
			Registry.ModBlocks.WHITE_ARROW_U_TURN_MARKING,
			Registry.ModBlocks.WHITE_ZEBRA_CROSSING_MARKING,

			Registry.ModBlocks.WHITE_CENTER_DASH_MARKING,
			Registry.ModBlocks.WHITE_INFILL_MARKING,
			Registry.ModBlocks.WHITE_CENTER_TURN_MARKING,
			Registry.ModBlocks.WHITE_CENTER_THICK,
			Registry.ModBlocks.WHITE_CENTER_STUB_SHORT,
			Registry.ModBlocks.WHITE_CENTER_STUB_MEDIUM,
			Registry.ModBlocks.WHITE_CENTER_STUB_LONG,

			Registry.ModBlocks.WHITE_EDGE_TURN_MARKING_INSIDE,
			Registry.ModBlocks.WHITE_EDGE_TURN_MARKING_OUTSIDE,
			Registry.ModBlocks.WHITE_EDGE_DASH_MARKING,
			Registry.ModBlocks.WHITE_EDGE_MARKING,
			Registry.ModBlocks.WHITE_EDGE_THICK,
			Registry.ModBlocks.WHITE_EDGE_STUB_SHORT_LEFT,
			Registry.ModBlocks.WHITE_EDGE_STUB_MEDIUM_LEFT,
			Registry.ModBlocks.WHITE_EDGE_STUB_LONG_LEFT,
			Registry.ModBlocks.WHITE_EDGE_STUB_SHORT_RIGHT,
			Registry.ModBlocks.WHITE_EDGE_STUB_MEDIUM_RIGHT,
			Registry.ModBlocks.WHITE_EDGE_STUB_LONG_RIGHT,

			Registry.ModBlocks.WHITE_T_CENTER_LONG,
			Registry.ModBlocks.WHITE_T_LEFT_LONG,
			Registry.ModBlocks.WHITE_T_RIGHT_LONG,

			Registry.ModBlocks.WHITE_T_CENTER,
			Registry.ModBlocks.WHITE_T_CENTER_LEFT,
			Registry.ModBlocks.WHITE_T_CENTER_RIGHT,

			Registry.ModBlocks.WHITE_T_CENTER_SHORT,
			Registry.ModBlocks.WHITE_T_SHORT_LEFT,
			Registry.ModBlocks.WHITE_T_SHORT_RIGHT,

			Registry.ModBlocks.WHITE_L_THIN_LEFT,
			Registry.ModBlocks.WHITE_L_THIN_RIGHT,
			Registry.ModBlocks.WHITE_L_LEFT,
			Registry.ModBlocks.WHITE_L_RIGHT,
			Registry.ModBlocks.WHITE_L_SHORT_LEFT,
			Registry.ModBlocks.WHITE_L_SHORT_RIGHT
		)

		ModelLoader()
	}
}