package me.znepb.zrm

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.impl.event.interaction.InteractionEventsRouter
import net.minecraft.server.network.ServerPlayerInteractionManager
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

		PlayerBlockBreakEvents.BEFORE
	}
}