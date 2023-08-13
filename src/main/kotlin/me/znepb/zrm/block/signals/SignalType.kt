package me.znepb.zrm.block.signals

import me.znepb.zrm.Registry
import net.minecraft.block.entity.BlockEntityType

enum class SignalType(
    val type: String,
    val lights: List<SignalLight>,
) {
    THREE_HEAD("three_head", listOf(SignalLight.GREEN, SignalLight.YELLOW, SignalLight.RED)),
    THREE_HEAD_LEFT("three_head_left", listOf(SignalLight.GREEN_LEFT, SignalLight.YELLOW_LEFT, SignalLight.RED_LEFT)),
    THREE_HEAD_RIGHT("three_head_right", listOf(SignalLight.GREEN_RIGHT, SignalLight.YELLOW_RIGHT, SignalLight.RED_RIGHT)),
    THREE_HEAD_STRAIGHT("three_head_straight", listOf(SignalLight.GREEN_STRAIGHT, SignalLight.YELLOW_STRAIGHT, SignalLight.RED_STRAIGHT));

    companion object {
        fun fromType(type: String): SignalType? {
            return when (type) {
                "three_head" -> THREE_HEAD
                "three_head_left" -> THREE_HEAD_LEFT
                "three_head_right" -> THREE_HEAD_RIGHT
                "three_head_straight" -> THREE_HEAD_STRAIGHT
                else -> null
            }
        }
    }
}
