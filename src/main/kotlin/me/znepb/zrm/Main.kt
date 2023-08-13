package me.znepb.zrm

import me.znepb.zrm.block.cabinet.TrafficCabinetBlockEntity
import me.znepb.zrm.block.signals.AbstractTrafficSignalBlockEntity
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
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

		PlayerBlockBreakEvents.BEFORE.register { world: World, player: PlayerEntity, pos: BlockPos, state: BlockState, blockEntity: BlockEntity? ->
			if(blockEntity is AbstractTrafficSignalBlockEntity) { blockEntity.remove() }
			if(blockEntity is TrafficCabinetBlockEntity) { blockEntity.remove() }

			true
		}
	}
}