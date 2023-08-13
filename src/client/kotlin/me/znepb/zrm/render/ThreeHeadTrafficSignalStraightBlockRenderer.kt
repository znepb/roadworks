package me.znepb.zrm.render

import me.znepb.zrm.block.signals.SignalLight
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory

class ThreeHeadTrafficSignalStraightBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    AbstractThreeHeadSignalBlockRenderer(
        ctx, SignalLight.RED_STRAIGHT, SignalLight.YELLOW_STRAIGHT, SignalLight.GREEN_STRAIGHT
    )