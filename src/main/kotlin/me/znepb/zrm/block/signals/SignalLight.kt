package me.znepb.zrm.block.signals

import me.znepb.zrm.Registry
import net.minecraft.block.Block

enum class SignalLight(val light: String, val genericType: SignalLight, val isGeneric: Boolean) {
    GREEN("green", GREEN, true),
    YELLOW("yellow", YELLOW, true),
    RED("red", RED, true),
    GREEN_LEFT("green_left", GREEN, false),
    YELLOW_LEFT("yellow_left", YELLOW, false),
    RED_LEFT("red_left", RED, false),
    GREEN_STRAIGHT("green_straight", GREEN, false),
    YELLOW_STRAIGHT("yellow_straight", YELLOW, false),
    RED_STRAIGHT("red_straight", RED, false),
    GREEN_RIGHT("green_right", GREEN, false),
    YELLOW_RIGHT("yellow_right", YELLOW, false),
    RED_RIGHT("red_right", RED, false);

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

        fun getReds(block: Block): List<SignalLight> {
            return when(block) {
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL -> listOf(RED)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT -> listOf(RED_LEFT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT -> listOf(RED_RIGHT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT -> listOf(RED_STRAIGHT)
                else -> listOf()
            }
        }

        fun getGreens(block: Block): List<SignalLight> {
            return when(block) {
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL -> listOf(GREEN)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT -> listOf(GREEN_LEFT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT -> listOf(GREEN_RIGHT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT -> listOf(GREEN_STRAIGHT)
                else -> listOf()
            }
        }

        fun getYellows(block: Block): List<SignalLight> {
            return when(block) {
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL -> listOf(YELLOW)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT -> listOf(YELLOW_LEFT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT -> listOf(YELLOW_RIGHT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT -> listOf(YELLOW_STRAIGHT)
                else -> listOf()
            }
        }
    }
}