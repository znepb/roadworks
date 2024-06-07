package me.znepb.roadworks.block.signals

enum class SignalType(
    val type: String,
    val lights: List<SignalLight>,
) {
    PEDESTRIAN("pedestrian", listOf(SignalLight.DONT_WALK, SignalLight.WALK)),
    ONE_HEAD_GREEN("green_beacon", listOf(SignalLight.GREEN)),
    ONE_HEAD_YELLOW("yellow_beacon", listOf(SignalLight.YELLOW)),
    ONE_HEAD_RED("rec_beacon", listOf(SignalLight.RED)),
    THREE_HEAD("three_head", listOf(SignalLight.GREEN, SignalLight.YELLOW, SignalLight.RED)),
    THREE_HEAD_LEFT("three_head_left", listOf(SignalLight.GREEN_LEFT, SignalLight.YELLOW_LEFT, SignalLight.RED_LEFT)),
    THREE_HEAD_RIGHT("three_head_right", listOf(SignalLight.GREEN_RIGHT, SignalLight.YELLOW_RIGHT, SignalLight.RED_RIGHT)),
    THREE_HEAD_STRAIGHT("three_head_straight", listOf(SignalLight.GREEN_STRAIGHT, SignalLight.YELLOW_STRAIGHT, SignalLight.RED_STRAIGHT)),
    FIVE_HEAD_LEFT("five_head_left", listOf(SignalLight.GREEN_LEFT, SignalLight.YELLOW_LEFT, SignalLight.GREEN, SignalLight.YELLOW, SignalLight.RED)),
    FIVE_HEAD_RIGHT("five_head_right", listOf(SignalLight.GREEN_RIGHT, SignalLight.YELLOW_RIGHT, SignalLight.GREEN, SignalLight.YELLOW, SignalLight.RED));

    companion object {
        fun fromType(type: String): SignalType? {
            entries.forEach {
                if(it.type == type) return it
            }

            return null
        }
    }
}
