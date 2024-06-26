package me.znepb.roadworks.render

import me.znepb.roadworks.block.signals.SignalLight
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory

class ThreeHeadTrafficSignalLeftBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    AbstractThreeHeadSignalBlockRenderer(
        ctx, SignalLight.RED_LEFT, SignalLight.YELLOW_LEFT, SignalLight.GREEN_LEFT
    )