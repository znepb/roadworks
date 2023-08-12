package me.znepb.zrm.block

import me.znepb.zrm.Registry
import me.znepb.zrm.block.post.AbstractPostMountableBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

open class SignBlockEntity(pos: BlockPos, state: BlockState)
    : AbstractPostMountableBlockEntity(Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, pos, state)