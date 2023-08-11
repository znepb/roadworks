package me.znepb.zrm

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Main : ModInitializer {
	val NAMESPACE = "zrm"
    val logger = LoggerFactory.getLogger(NAMESPACE)

	fun ModId(id: String): Identifier {
		return Identifier(NAMESPACE, id)
	}

	override fun onInitialize() {
		logger.info("zrm is initalizing")

		Registry.init()
	}
}