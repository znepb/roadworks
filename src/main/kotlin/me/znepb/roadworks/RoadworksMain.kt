package me.znepb.roadworks

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer
import me.znepb.roadworks.cabinet.TrafficCabinetBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.network.DestroyAttachmentPacket
import me.znepb.roadworks.network.EditRouteShieldPacket
import me.znepb.roadworks.network.EditSignPacket
import me.znepb.roadworks.sign.SignageManager
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.slf4j.LoggerFactory

object RoadworksMain : ModInitializer {
	val NAMESPACE = "roadworks"
	val logger = LoggerFactory.getLogger(NAMESPACE)

	val signageManager = SignageManager()
	var CONFIG: Config? = null

	fun ModId(id: String): Identifier {
		return Identifier(NAMESPACE, id)
	}

	override fun onInitialize() {
		logger.info("Roadworks is initializing")

		AutoConfig.register(Config::class.java, ::Toml4jConfigSerializer)
		RoadworksRegistry.init()

		CONFIG = AutoConfig.getConfigHolder(Config::class.java).config

		PlayerBlockBreakEvents.BEFORE.register { world: World, player: PlayerEntity, pos: BlockPos, state: BlockState, blockEntity: BlockEntity? ->
			if (blockEntity is PostContainerBlockEntity) {
				blockEntity.remove()
			} else if (blockEntity is TrafficCabinetBlockEntity) {
				blockEntity.remove()
			}
			true
		}

		// Networking Registration
		EditSignPacket.register()
		DestroyAttachmentPacket.register()
		EditRouteShieldPacket.register()
	}
}