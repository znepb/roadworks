package me.znepb.zrm.render

import me.znepb.zrm.block.signals.SignalLight
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory

class ThreeHeadTrafficSignalRightBlockRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    AbstractThreeHeadSignalBlockRenderer(
        ctx, SignalLight.RED_RIGHT, SignalLight.YELLOW_RIGHT, SignalLight.GREEN_RIGHT
    )