package me.znepb.roadworks.signal

enum class SignalType(
    val type: String,
    val lights: List<SignalLight>,
) {
    PEDESTRIAN("pedestrian", listOf(SignalLight.DONT_WALK, SignalLight.WALK)),
    BEACON_GREEN("green_beacon", listOf(SignalLight.GREEN)),
    BEACON_YELLOW("yellow_beacon", listOf(SignalLight.YELLOW)),
    BEACON_RED("red_beacon", listOf(SignalLight.RED)),
    THREE_HEAD("three_head", listOf(SignalLight.GREEN, SignalLight.YELLOW, SignalLight.RED)),
    THREE_HEAD_LEFT("three_head_left", listOf(SignalLight.GREEN_LEFT, SignalLight.YELLOW_LEFT, SignalLight.RED_LEFT)),
    THREE_HEAD_RIGHT("three_head_right", listOf(
        SignalLight.GREEN_RIGHT,
        SignalLight.YELLOW_RIGHT,
        SignalLight.RED_RIGHT
    )),
    THREE_HEAD_STRAIGHT("three_head_straight", listOf(
        SignalLight.GREEN_STRAIGHT,
        SignalLight.YELLOW_STRAIGHT,
        SignalLight.RED_STRAIGHT
    )),
    FIVE_HEAD_LEFT("five_head_left", listOf(
        SignalLight.GREEN_LEFT,
        SignalLight.YELLOW_LEFT,
        SignalLight.GREEN,
        SignalLight.YELLOW,
        SignalLight.RED
    )),
    FIVE_HEAD_RIGHT("five_head_right", listOf(
        SignalLight.GREEN,
        SignalLight.YELLOW,
        SignalLight.GREEN_RIGHT,
        SignalLight.YELLOW_RIGHT,
        SignalLight.RED
    )),
    FIVE_HEAD_LEFT_RIGHT("five_head_left_right", listOf(
        SignalLight.GREEN_LEFT,
        SignalLight.YELLOW_LEFT,
        SignalLight.GREEN_RIGHT,
        SignalLight.YELLOW_RIGHT,
        SignalLight.RED
    )),
    FLASHING_YELLOW_ARROW_SIGNAL("flashing_yellow_arrow_signal", listOf(
        SignalLight.GREEN_LEFT,
        SignalLight.FLASHING_YELLOW_LEFT,
        SignalLight.YELLOW_LEFT,
        SignalLight.RED_LEFT
    ));

    fun getReds(): List<SignalLight> {
        return this.lights.filter {
            if(it.isGeneric && it == SignalLight.RED) true else it.genericType === SignalLight.RED
        }
    }

    fun getYellows(): List<SignalLight> {
        return this.lights.filter {
            if(it.isGeneric && it == SignalLight.YELLOW) true else it.genericType === SignalLight.YELLOW
        }
    }

    fun getGreens(): List<SignalLight> {
        return this.lights.filter {
            if(it.isGeneric && it == SignalLight.GREEN) true else it.genericType === SignalLight.GREEN
        }
    }

    companion object {
        fun fromType(type: String): SignalType? {
            entries.forEach {
                if(it.type == type) return it
            }

            return null
        }
    }
}
