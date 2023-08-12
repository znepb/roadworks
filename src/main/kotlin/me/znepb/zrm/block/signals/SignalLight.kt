package me.znepb.zrm.block.signals

enum class SignalLight(val light: String) {
    GREEN("green"),
    YELLOW("yellow"),
    RED("red"),
    GREEN_LEFT("green_left"),
    YELLOW_LEFT("yellow_left"),
    RED_LEFT("red_left"),
    GREEN_STRAIGHT("green_straight"),
    YELLOW_STRAIGHT("yellow_straight"),
    RED_STRAIGHT("red_straight"),
    GREEN_RIGHT("green_right"),
    YELLOW_RIGHT("yellow_right"),
    RED_RIGHT("red_right");

    companion object {
        fun fromName(name: String): SignalLight? {
            return when (name) {
                "green" -> GREEN
                "yellow" -> YELLOW
                "red" -> RED
                "green_right" -> GREEN_RIGHT
                "yellow_right" -> YELLOW_RIGHT
                "red_right" -> RED_RIGHT
                "green_straight" -> GREEN_STRAIGHT
                "yellow_straight" -> YELLOW_STRAIGHT
                "red_straight" -> RED_STRAIGHT
                "green_left" -> GREEN_LEFT
                "yellow_left" -> YELLOW_LEFT
                "red_left" -> RED_LEFT
                else -> null
            }
        }
    }
}