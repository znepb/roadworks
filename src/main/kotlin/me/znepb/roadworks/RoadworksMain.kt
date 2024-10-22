package me.znepb.roadworks

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import me.znepb.roadworks.block.Linkable
import me.znepb.roadworks.block.cabinet.TrafficCabinetBlockEntity
import me.znepb.roadworks.block.sign.SignType
import me.znepb.roadworks.network.EditSignPacket
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.Event.DEFAULT_PHASE
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.RegistryOps
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.slf4j.LoggerFactory


object RoadworksMain : ModInitializer {
	val NAMESPACE = "roadworks"
	val logger = LoggerFactory.getLogger(NAMESPACE)

	val SIGN_TYPES = Object2ObjectOpenHashMap<Identifier, SignType>()

	fun ModId(id: String): Identifier {
		return Identifier(NAMESPACE, id)
	}

	override fun onInitialize() {
		logger.info("Roadworks is initalizing")

		Registry.init()

		PlayerBlockBreakEvents.BEFORE.register { world: World, player: PlayerEntity, pos: BlockPos, state: BlockState, blockEntity: BlockEntity? ->
			if (blockEntity is Linkable) {
				blockEntity.remove()
			}
			if (blockEntity is TrafficCabinetBlockEntity) {
				blockEntity.remove()
			}

			true
		}

		val id = ModId("early_reload")

		// Server Event Registries
		ServerLifecycleEvents.SERVER_STARTED.addPhaseOrdering(id, DEFAULT_PHASE)
		ServerLifecycleEvents.SERVER_STARTED.register(id, RoadworksMain::loadCustomSignage)

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.addPhaseOrdering(id, DEFAULT_PHASE)
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(id) { minecraftServer, _, _ ->
			loadCustomSignage(minecraftServer)
		}

		// Networking Registration
		EditSignPacket.register()
	}

	private fun addCustomSign(server: MinecraftServer, identifier: Identifier, type: SignType) {
		logger.debug("Loading custom sign {}", identifier)
		SIGN_TYPES[identifier] = type
	}

	private fun loadCustomSignage(server: MinecraftServer) {
		logger.info("Reloading custom content")

		val ops = RegistryOps.of(JsonOps.INSTANCE, server.registryManager)
		val manager = server.resourceManager

		for (res in manager.findResources("signs") { it.path.endsWith(".json") }.entries) {
			val id = Identifier(
				res.key.namespace,
				res.key.path.substring("signs/".length, res.key.path.length - 5)
			)

			try {
				val signType = SignType.CODEC.decode(ops, JsonParser.parseReader(res.value.reader)).getOrThrow(false) {}
				addCustomSign(server, id, signType.first)
			} catch (e: Exception) {
				logger.warn("{} is invalid", res.key.toString())
				e.printStackTrace()
			}
		}
	}
}