package me.znepb.zrm

import me.znepb.zrm.Main.NAMESPACE
import me.znepb.zrm.init.ModelLoader
import me.znepb.zrm.render.*
import me.znepb.zrm.render.ThreeHeadTrafficSignalLeftBlockRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier
import net.minecraft.block.Block
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object ZrmClient : ClientModInitializer {
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

		ModelLoader()
	}
}