package me.znepb.roadworks.util

import me.znepb.roadworks.misc.Catwalk
import net.minecraft.util.StringIdentifiable

enum class PostThickness(val id: Int, name: String, val thickness: Double) : StringIdentifiable {
    THIN(1, "thin", 2.0 / 16.0),
    MEDIUM(2, "medium", 4.0 / 16.0),
    THICK(3, "thick", 6.0 / 16.0),
    NONE(0, "none", 0.0);


    override fun toString(): String {
        return asString()
    }

    override fun asString(): String {
        return this.name.lowercase()
    }

    companion object {
        fun fromId(id: Int): PostThickness {
            return when(id) {
                1 -> THIN
                2 -> MEDIUM
                3 -> THICK
                else -> NONE
            }
        }

        fun fromName(name: String): PostThickness {
            return when(name.lowercase()) {
                "thin" -> THIN
                "medium" -> MEDIUM
                "thick" -> THICK
                else -> NONE
            }
        }

        fun fromNameNullable(name: String): PostThickness? {
            return when(name.lowercase()) {
                "thin" -> THIN
                "medium" -> MEDIUM
                "thick" -> THICK
                "none" -> NONE
                else -> null
            }
        }
    }
}