package me.znepb.roadworks.container


import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

abstract class AttachmentContainer(settings: Settings) : BlockWithEntity(settings), BlockEntityProvider {
    override fun isTransparent(state: BlockState?, world: BlockView?, pos: BlockPos?) = true

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        val be = world.getBlockEntity(pos)
        if(be is AttachmentContainerBlockEntity) {
            val attachment = be.getAttachmentHit(hit)
            return if(attachment != null) attachment.onUse(player, hand, hit) ?: ActionResult.PASS else be.onUse(player, hand, hit)
        }
        return ActionResult.PASS
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        val be = world.getBlockEntity(pos)
        if(be is AttachmentContainerBlockEntity) be.remove()
        super.onBreak(world, pos, state, player)
    }
}