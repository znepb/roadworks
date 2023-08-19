package me.znepb.roadworks

import me.znepb.roadworks.block.cabinet.TrafficCabinetBlockEntity
import me.znepb.roadworks.block.signals.AbstractTrafficSignalBlockEntity
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

	fun ModId(id: String): Identifier {
		return Identifier(NAMESPACE, id)
	}

	override fun onInitialize() {
		logger.info("Roadworks is initalizing")

		Registry.init()

		PlayerBlockBreakEvents.BEFORE.register { world: World, player: PlayerEntity, pos: BlockPos, state: BlockState, blockEntity: BlockEntity? ->
			if(blockEntity is AbstractTrafficSignalBlockEntity) { blockEntity.remove() }
			if(blockEntity is TrafficCabinetBlockEntity) { blockEntity.remove() }

			true
		}
	}
}