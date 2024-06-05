package me.znepb.roadworks.render

import me.znepb.roadworks.block.signals.SignalLight
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory

class RedBeaconRenderer(private val ctx: BlockEntityRendererFactory.Context) :
    AbstractBeaconRenderer(
        ctx, SignalLight.RED
    )