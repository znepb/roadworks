package me.znepb.zrm.block.signals

import me.znepb.zrm.Registry
import net.minecraft.block.entity.BlockEntityType

enum class SignalType(
    val type: String,
    val lights: List<SignalLight>,
) {
    THREE_HEAD("three_head", listOf(SignalLight.GREEN, SignalLight.YELLOW, SignalLight.RED));

    companion object {
        fun fromType(type: String): SignalType? {
            return when (type) {
                "three_head" -> THREE_HEAD
                else -> null
            }
        }
    }
}
