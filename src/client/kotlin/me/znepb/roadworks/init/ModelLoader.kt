package me.znepb.roadworks.init

import me.znepb.roadworks.RoadworksClient.logger
import me.znepb.roadworks.render.*
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin

class ModelLoader {
    companion object {
        val models = listOf(
            PostBlockRenderer.POST_THIN_EXT_MODEL,
            PostBlockRenderer.POST_THIN_FOOTER_MODEL,
            PostBlockRenderer.POST_THIN_MID_MODEL,
            PostBlockRenderer.POST_THICK_EXT_MODEL,
            PostBlockRenderer.POST_THICK_FOOTER_MODEL,
            PostBlockRenderer.POST_THICK_MID_MODEL,
            PostBlockRenderer.POST_MEDIUM_EXT_MODEL,
            PostBlockRenderer.POST_MEDIUM_FOOTER_MODEL,
            PostBlockRenderer.POST_MEDIUM_MID_MODEL,
            AbstractBeaconRenderer.SIGNAL_FRAME_1,
            AbstractFiveHeadSignalBlockRenderer.SIGNAL_FRAME_5,
            AbstractThreeHeadSignalBlockRenderer.SIGNAL_FRAME_3,
            AbstractThreeHeadSignalBlockRenderer.SIGNAL,
            PedestrianSignalRenderer.DONT_WALK,
            PedestrianSignalRenderer.WALK
        )
    }

    init {
        ModelLoadingPlugin.register { plugin ->
            run {
                logger.info("Registering block models")
                plugin.addModels(models)
                plugin.addModels(SignalRenderer.SIGNAL_MODEL_IDS)
            }
        }
    }
}