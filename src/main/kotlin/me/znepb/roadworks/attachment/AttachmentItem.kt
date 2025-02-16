package me.znepb.roadworks.attachment

import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.container.WallContainer
import me.znepb.roadworks.util.BlockItemUtils
import me.znepb.roadworks.util.PostThickness
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemUsageContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.world.event.GameEvent

open class AttachmentItem(val attachment: AttachmentType<out Attachment>, var settings: Settings) : Item(settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val be = context.world.getBlockEntity(context.blockPos)
        if(be is PostContainerBlockEntity) {
            return useOnPostContainer(context)
        } else if(context.side != Direction.UP && context.side != Direction.DOWN) {
            return useOnWall(context)
        }

        return ActionResult.FAIL
    }

    private fun useOnWall(context: ItemUsageContext): ActionResult {
        val placementContext = ItemPlacementContext(context)
        if(!placementContext.canPlace()) return ActionResult.FAIL

        val placeAt = context.blockPos.offset(context.side, 1)
        val world = context.world
        val player = context.player
        val defaultState = RoadworksRegistry.ModBlocks.WALL_CONTAINER.defaultState
        val soundGroup = defaultState.soundGroup

        if(!BlockItemUtils.canPlace(placementContext, defaultState)) return ActionResult.FAIL
        if(!world.setBlockState(placeAt, defaultState)) return ActionResult.FAIL

        world.playSound(
            player,
            placeAt,
            RoadworksRegistry.ModBlocks.WALL_CONTAINER.defaultState.soundGroup.placeSound,
            SoundCategory.BLOCKS,
            (soundGroup.getVolume() + 1.0f) / 2.0f,
            soundGroup.getPitch() * 0.8f
        )
        world.emitGameEvent(GameEvent.BLOCK_PLACE, placeAt, GameEvent.Emitter.of(player, defaultState))
        if (player == null || !player.abilities.creativeMode) {
            context.stack.decrement(1)
        }

        val container = WallContainer.blockEntity(world, placeAt)
        if(container != null) {
            logger.info("Placing time")
            val attachment = attachment.factory.create(container)
            attachment.readNBT(context.stack.orCreateNbt)
            container.addAttachment(attachment, context.side)
        }

        return ActionResult.SUCCESS
    }

    private fun useOnPostContainer(context: ItemUsageContext): ActionResult {
        val be = context.world.getBlockEntity(context.blockPos)
        if(be !is PostContainerBlockEntity) return ActionResult.FAIL

        if(context.side == Direction.UP || context.side == Direction.DOWN) return ActionResult.FAIL
        if(be.getAttachmentsOnFace(context.side).isNotEmpty()) return ActionResult.FAIL
        if(be.getAttachmentsOnFace(context.side.rotateYClockwise()).isNotEmpty()) return ActionResult.FAIL
        if(be.getAttachmentsOnFace(context.side.rotateYCounterclockwise()).isNotEmpty()) return ActionResult.FAIL
        if(be.getDirectionThickness(context.side) != PostThickness.NONE) return ActionResult.FAIL

        context.world.playSoundAtBlockCenter(context.blockPos, SoundEvent.of(Identifier("block.stone.place")), SoundCategory.BLOCKS, 1.0F, 0.75F, true)

        if(context.world.isClient) return ActionResult.PASS

        // do all actual handing on the server

        val attachment = attachment.factory.create(be)
        attachment.readNBT(context.stack.orCreateNbt)
        be.addAttachment(attachment, context.side)
        return ActionResult.SUCCESS
    }
}