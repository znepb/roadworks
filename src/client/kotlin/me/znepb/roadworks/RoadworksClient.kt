package me.znepb.roadworks

import me.znepb.roadworks.RoadworksMain.NAMESPACE
import me.znepb.roadworks.init.ModelLoader
import me.znepb.roadworks.render.*
import me.znepb.roadworks.render.ThreeHeadTrafficSignalLeftBlockRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.gui.screen.ingame.ForgingScreen
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.client.render.RenderLayer
import org.slf4j.LoggerFactory

object RoadworksClient : ClientModInitializer {
	val logger = LoggerFactory.getLogger(NAMESPACE)

	override fun onInitializeClient() {
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.POST_BLOCK_ENTITY, ::PostBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, ::SignBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY, ::ThreeHeadTrafficSignalBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_LEFT_BLOCK_ENTITY, ::ThreeHeadTrafficSignalLeftBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT_BLOCK_ENTITY, ::ThreeHeadTrafficSignalStraightBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT_BLOCK_ENTITY, ::ThreeHeadTrafficSignalRightBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT_BLOCK_ENTITY, ::FiveHeadTrafficSignalLeftBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT_BLOCK_ENTITY, ::FiveHeadTrafficSignalRightBlockRenderer)

		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
			Registry.ModBlocks.WHITE_CENTER_MARKING,
			Registry.ModBlocks.WHITE_CENTER_DASH_MARKING,
			Registry.ModBlocks.WHITE_INFILL_MARKING,
			Registry.ModBlocks.WHITE_CENTER_TURN_MARKING,
			Registry.ModBlocks.WHITE_EDGE_TURN_MARKING_INSIDE,
			Registry.ModBlocks.WHITE_EDGE_TURN_MARKING_OUTSIDE,
			Registry.ModBlocks.WHITE_EDGE_DASH_MARKING,
			Registry.ModBlocks.WHITE_EDGE_MARKING,
			Registry.ModBlocks.WHITE_T_CENTER
		)

		ModelLoader()
	}
}