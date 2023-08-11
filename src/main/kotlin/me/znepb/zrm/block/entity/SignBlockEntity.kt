package me.znepb.zrm.block.entity

import me.znepb.zrm.Registry
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

open class SignBlockEntity(pos: BlockPos, state: BlockState)
    : PostMountableBlockEntity(Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, pos, state)