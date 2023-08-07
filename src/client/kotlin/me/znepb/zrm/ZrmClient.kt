package me.znepb.zrm

import me.znepb.zrm.block.PostBlockRenderer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

object ZrmClient : ClientModInitializer {
	override fun onInitializeClient() {
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.POST_BLOCK_ENTITY, ::PostBlockRenderer)
	}
}