package me.znepb.roadworks.block.signals

import me.znepb.roadworks.Registry
import net.minecraft.block.Block

enum class SignalLight(val light: String, val genericType: SignalLight, val isGeneric: Boolean, val signalDirection: SignalDirection) {
    GREEN("green", GREEN, true, SignalDirection.NONE),
    YELLOW("yellow", YELLOW, true, SignalDirection.NONE),
    RED("red", RED, true, SignalDirection.NONE),
    GREEN_LEFT("green_left", GREEN, false, SignalDirection.LEFT),
    YELLOW_LEFT("yellow_left", YELLOW, false, SignalDirection.LEFT),
    RED_LEFT("red_left", RED, false, SignalDirection.LEFT),
    GREEN_STRAIGHT("green_straight", GREEN, false, SignalDirection.STRAIGHT),
    YELLOW_STRAIGHT("yellow_straight", YELLOW, false, SignalDirection.STRAIGHT),
    RED_STRAIGHT("red_straight", RED, false, SignalDirection.STRAIGHT),
    GREEN_RIGHT("green_right", GREEN, false, SignalDirection.RIGHT),
    YELLOW_RIGHT("yellow_right", YELLOW, false, SignalDirection.RIGHT),
    RED_RIGHT("red_right", RED, false, SignalDirection.RIGHT);

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

        fun getReds(block: Block): List<SignalLight> {
            return when(block) {
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL -> listOf(RED)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT -> listOf(RED_LEFT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT -> listOf(RED_RIGHT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT -> listOf(RED_STRAIGHT)
                Registry.ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT -> listOf(RED)
                Registry.ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT -> listOf(RED)
                else -> listOf()
            }
        }

        fun getGreens(block: Block): List<SignalLight> {
            return when(block) {
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL -> listOf(GREEN)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT -> listOf(GREEN_LEFT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT -> listOf(GREEN_RIGHT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT -> listOf(GREEN_STRAIGHT)
                Registry.ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT -> listOf(GREEN_LEFT, GREEN)
                Registry.ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT -> listOf(GREEN_RIGHT, GREEN)
                else -> listOf()
            }
        }

        fun getYellows(block: Block): List<SignalLight> {
            return when(block) {
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL -> listOf(YELLOW)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT -> listOf(YELLOW_LEFT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT -> listOf(YELLOW_RIGHT)
                Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT -> listOf(YELLOW_STRAIGHT)
                Registry.ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT -> listOf(YELLOW_LEFT, YELLOW)
                Registry.ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT -> listOf(YELLOW_RIGHT, YELLOW)
                else -> listOf()
            }
        }
    }
}