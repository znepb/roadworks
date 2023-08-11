package me.znepb.zrm.util

import me.znepb.zrm.Registry
import net.minecraft.block.Block
import net.minecraft.block.BlockState

enum class PostThickness(val id: Int, name: String) {
    THIN(1, "thin"),
    MEDIUM(2, "medium"),
    THICK(3, "thick"),
    NONE(0, "none");

    companion object {
        fun fromId(id: Int): PostThickness {
            return when(id) {
                1 -> THIN
                2 -> MEDIUM
                3 -> THICK
                else -> NONE
            }
        }

        fun fromState(state: BlockState): PostThickness {
            return if(state.isOf(Registry.ModBlocks.THICK_POST)) THICK
                   else if(state.isOf(Registry.ModBlocks.POST)) MEDIUM
                   else if(state.isOf(Registry.ModBlocks.THIN_POST)) THIN
                   else NONE
        }

        fun fromName(name: String): PostThickness {
            return when(name) {
                "thin" -> THIN
                "medium" -> MEDIUM
                "thick" -> THICK
                else -> NONE
            }
        }
    }
}