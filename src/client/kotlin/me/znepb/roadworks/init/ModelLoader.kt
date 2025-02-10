package me.znepb.roadworks.init

import me.znepb.roadworks.RoadworksClient.logger
import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.datagen.ModelProvider
import me.znepb.roadworks.render.PostContainerRenderer
import me.znepb.roadworks.render.attachments.*
import me.znepb.roadworks.render.models.PostItemBakedModel
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin

class ModelLoader {
    companion object {
        val models = mutableListOf(
            PostContainerRenderer.POST_THIN_EXT_MODEL,
            PostContainerRenderer.POST_THIN_FOOTER_MODEL,
            PostContainerRenderer.POST_THIN_MID_MODEL,
            PostContainerRenderer.POST_THIN_STUB_MODEL,
            PostContainerRenderer.POST_THICK_EXT_MODEL,
            PostContainerRenderer.POST_THICK_FOOTER_MODEL,
            PostContainerRenderer.POST_THICK_MID_MODEL,
            PostContainerRenderer.POST_THICK_STUB_MODEL,
            PostContainerRenderer.POST_MEDIUM_EXT_MODEL,
            PostContainerRenderer.POST_MEDIUM_FOOTER_MODEL,
            PostContainerRenderer.POST_MEDIUM_MID_MODEL,
            PostContainerRenderer.POST_MEDIUM_STUB_MODEL,
            BeaconAttachmentRenderer.SIGNAL_FRAME_1,
            ThreeHeadSignalAttachmentRenderer.SIGNAL_FRAME_3,
            FiveHeadSignalAttachmentRenderer.SIGNAL_FRAME_5,
            PedestrianSignalAttachmentRenderer.BLANK_SIGNAL,
            PedestrianSignalAttachmentRenderer.DONT_WALK,
            PedestrianSignalAttachmentRenderer.WALK,
            PostItemBakedModel.ITEM_THICK,
            PostItemBakedModel.ITEM_MEDIUM,
            PostItemBakedModel.ITEM_THIN,
            TrainBellAttachmentRenderer.TRAIN_BELL,
            TrainSignalAttachmentRenderer.BEACON_BASE,
            TrainSignalAttachmentRenderer.BEACON_BACKBEAM,
            TrainSignalAttachmentRenderer.BEACON_OFF,
            TrainSignalAttachmentRenderer.BEACON_ON,
            CrossingGateAttachmentRenderer.HINGE,
            CrossingGateAttachmentRenderer.GATE_ARM
        )

        init {
            SignalRenderer.SIGNAL_MODEL_IDS.forEach {
                models.add(it)
            }
        }
    }

    init {

        ModelLoadingPlugin.register { plugin ->
            run {
                logger.info("Registering block models")
                plugin.addModels(models)
                plugin.addModels(ModelProvider.signals.map { ModId("block/signal_$it") })
            }
        }
    }
}