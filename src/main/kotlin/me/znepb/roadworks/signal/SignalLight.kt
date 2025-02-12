package me.znepb.roadworks.signal

enum class SignalLight(val light: String, val genericType: SignalLight, val isGeneric: Boolean, val signalDirection: SignalDirection, val flashing: Boolean, val useTextureOf: SignalLight?) {
    GREEN("green", GREEN, true, SignalDirection.NONE, false, null),
    YELLOW("yellow", YELLOW, true, SignalDirection.NONE, false, null),
    RED("red", RED, true, SignalDirection.NONE, false, null),
    GREEN_LEFT("green_left", GREEN, false, SignalDirection.LEFT, false, null),
    YELLOW_LEFT("yellow_left", YELLOW, false, SignalDirection.LEFT, false, null),
    RED_LEFT("red_left", RED, false, SignalDirection.LEFT, false, null),
    GREEN_STRAIGHT("green_straight", GREEN, false, SignalDirection.STRAIGHT, false, null),
    YELLOW_STRAIGHT("yellow_straight", YELLOW, false, SignalDirection.STRAIGHT, false, null),
    RED_STRAIGHT("red_straight", RED, false, SignalDirection.STRAIGHT, false, null),
    GREEN_RIGHT("green_right", GREEN, false, SignalDirection.RIGHT, false, null),
    YELLOW_RIGHT("yellow_right", YELLOW, false, SignalDirection.RIGHT, false, null),
    RED_RIGHT("red_right", RED, false, SignalDirection.RIGHT, false, null),
    WALK("walk", GREEN, false, SignalDirection.NONE, false, null),
    DONT_WALK("dont_walk", RED, false, SignalDirection.NONE, false, null),
    FLASHING_YELLOW_LEFT("yellow_flashing_left", YELLOW, false, SignalDirection.LEFT, true, YELLOW_LEFT);

    enum class SignalDirection {
        STRAIGHT,
        LEFT,
        RIGHT,
        NONE;
    }

    companion object {
        fun fromName(name: String): SignalLight? {
            entries.forEach {
                if(it.name == name) return it
            }

            return null
        }
    }
}