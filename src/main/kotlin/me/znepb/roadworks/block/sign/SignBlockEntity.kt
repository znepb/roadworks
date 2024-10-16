package me.znepb.roadworks.block.sign

import me.znepb.roadworks.Registry
import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

class SignBlockEntity(pos: BlockPos, state: BlockState)
    : AbstractPostMountableBlockEntity(Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, pos, state)
{
    var signType = ModId("unknown")

    override fun writeExtraNBT(nbt: NbtCompound) {
        nbt.putString("sign_type", signType.toString())
        super.writeExtraNBT(nbt)
    }

    override fun readExtraNBT(nbt: NbtCompound) {
        val newSignType = Identifier.tryParse(nbt.getString("sign_type"))
        if(newSignType == null) {
            logger.warn("Invalid sign identifier: {}", nbt.getString(("sign_type")))
        } else {
            signType = newSignType
        }

        super.readExtraNBT(nbt)
    }
}