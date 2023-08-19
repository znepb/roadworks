package me.znepb.roadworks.render

import me.znepb.roadworks.block.signals.SignalLight
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory

class FiveHeadTrafficSignalRightBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    AbstractFiveHeadSignalBlockRenderer(
        ctx, SignalLight.RED, SignalLight.YELLOW, SignalLight.GREEN, SignalLight.YELLOW_RIGHT, SignalLight.GREEN_RIGHT
    )