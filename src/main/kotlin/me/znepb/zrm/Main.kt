package me.znepb.zrm

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Main : ModInitializer {
    val logger = LoggerFactory.getLogger("zrm")

	override fun onInitialize() {
		logger.info("zrm is initalizing")

		Registry.init()
	}
}