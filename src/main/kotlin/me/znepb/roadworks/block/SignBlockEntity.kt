package me.znepb.roadworks.block

import me.znepb.roadworks.Registry
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

open class SignBlockEntity(pos: BlockPos, state: BlockState)
    : AbstractPostMountableBlockEntity(Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, pos, state)