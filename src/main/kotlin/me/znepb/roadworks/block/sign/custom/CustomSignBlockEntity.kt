package me.znepb.roadworks.block.sign.custom

import me.znepb.roadworks.Registry
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import me.znepb.roadworks.util.Charset
import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos

class CustomSignBlockEntity(pos: BlockPos, state: BlockState)
    : AbstractPostMountableBlockEntity(Registry.ModBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY, pos, state)
{
    var color = "green"
    var contents = listOf<Charset>()

    override fun writeExtraNBT(nbt: NbtCompound) {
        val contents = mutableListOf<Int>()
        this.contents.forEach {
            contents.add(it.ordinal)
        }

        nbt.putString("color", color)
        nbt.putIntArray("contents", contents)
        super.writeExtraNBT(nbt)
    }

    override fun readExtraNBT(nbt: NbtCompound) {
        this.color = nbt.getString("color")
        val contents = nbt.getIntArray("contents")

        val newList = mutableListOf<Charset>()
        contents.forEach {
            newList.add(Charset.entries[it])
        }
        this.contents = newList

        super.readExtraNBT(nbt)
    }
}