package me.znepb.roadworks.render

import me.znepb.roadworks.block.signals.SignalLight
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory

class FiveHeadTrafficSignalLeftBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    AbstractFiveHeadSignalBlockRenderer(
        ctx, SignalLight.RED, SignalLight.YELLOW_LEFT, SignalLight.GREEN_LEFT, SignalLight.YELLOW, SignalLight.GREEN
    )