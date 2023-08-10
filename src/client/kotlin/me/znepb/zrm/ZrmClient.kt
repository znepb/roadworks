package me.znepb.zrm

import me.znepb.zrm.init.ModelLoader
import me.znepb.zrm.render.PostBlockRenderer
import me.znepb.zrm.render.SignBlockRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier
import net.minecraft.client.render.model.UnbakedModel
import org.slf4j.LoggerFactory

object ZrmClient : ClientModInitializer {
	val logger = LoggerFactory.getLogger("zrm")

	override fun onInitializeClient() {
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.POST_BLOCK_ENTITY, ::PostBlockRenderer)
		BlockEntityRendererFactories.register(Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, ::SignBlockRenderer)

		ModelLoader()
	}
}