package me.znepb.roadworks

import com.google.common.collect.ImmutableMap
import me.znepb.roadworks.RoadworksMain.NAMESPACE
import me.znepb.roadworks.attachment.Attachment
import me.znepb.roadworks.attachment.AttachmentType
import me.znepb.roadworks.container.PostContainer.Companion.createItemStackForThickness
import me.znepb.roadworks.gui.SignEditorScreen
import me.znepb.roadworks.init.ModelLoader
import me.znepb.roadworks.item.SignEditorScreenHandler
import me.znepb.roadworks.network.SyncContentPacketClient
import me.znepb.roadworks.render.PostContainerRenderer
import me.znepb.roadworks.render.WallContainerRenderer
import me.znepb.roadworks.render.attachments.*
import me.znepb.roadworks.util.PostThickness
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.item.ItemStack
import org.slf4j.LoggerFactory

object RoadworksClient : ClientModInitializer {
	val logger = LoggerFactory.getLogger(NAMESPACE)
	var attachmentRenderers: ImmutableMap<AttachmentType<out Attachment>, AttachmentRenderer<Attachment>> = ImmutableMap.of()

	override fun onInitializeClient() {
		logger.info("Roadworks is initializing")
		ModelLoadingPlugin.register( me.znepb.roadworks.ModelLoader() )

		SyncContentPacketClient.register()

		// Item Groups`
		ItemGroupEvents.modifyEntriesEvent(RoadworksRegistry.itemGroup).register {
			val roadSignAttachmentItem = ItemStack(RoadworksRegistry.ModItems.ROAD_SIGN_ATTACHMENT)
			roadSignAttachmentItem.orCreateNbt.putString("color", "green")
			it.add(roadSignAttachmentItem)

			val warningRoadSignAttachmentItem = ItemStack(RoadworksRegistry.ModItems.ROAD_SIGN_WARNING_ATTACHMENT)
			warningRoadSignAttachmentItem.orCreateNbt.putString("color", "yellow")
			it.add(warningRoadSignAttachmentItem)

			RoadworksMain.signageManager.getSignTypes().forEach { sign ->
				val item = ItemStack(RoadworksRegistry.ModItems.SIGN_ATTACHMENT)
				item.orCreateNbt.putString("sign_type", sign.key.toString())
				it.add(item)
			}

			// Generate posts for each thickness
			for(i in 1 .. 3) {
				it.add(createItemStackForThickness(PostThickness.fromId(i)))
			}
		}

		ClientPlayNetworking.registerGlobalReceiver(SignEditorScreenHandler.SyncData.PACKET_ID) { client, handler, buf, response ->
			val syncData = buf.decodeAsJson(SignEditorScreenHandler.SyncData.CODEC)

			client.execute {
				val screenHandler = MinecraftClient.getInstance().player?.currentScreenHandler
				if(screenHandler is SignEditorScreenHandler) {
					screenHandler.setBlockPosition(syncData.pos)
					screenHandler.setAttachmentUUID(syncData.uuid)
				}
			}
		}

		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.SIGN_ATTACHMENT, ::SignAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.ROAD_SIGN_ATTACHMENT, ::RoadSignAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.PEDESTRIAN_SIGNAL, ::PedestrianSignalAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.BEACON_GREEN, ::BeaconAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.BEACON_YELLOW, ::BeaconAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.BEACON_RED, ::BeaconAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT, ::ThreeHeadSignalAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT, ::ThreeHeadSignalAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_RIGHT, ::ThreeHeadSignalAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_STRAIGHT, ::ThreeHeadSignalAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.FLASHING_YELLOW_ARROW_SIGNAL_ATTACHMENT, ::FourHeadSignalAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT, ::FiveHeadSignalAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_RIGHT, ::FiveHeadSignalAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT_RIGHT, ::FiveHeadSignalAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.TRAIN_BELL, ::TrainBellAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.TRAIN_SIGNAL, ::TrainSignalAttachmentRenderer)
		AttachmentRendererFactories.register(RoadworksRegistry.ModAttachments.CROSSING_GATE, ::CrossingGateAttachmentRenderer)

		logger.info("Registering attachment renderers")
		attachmentRenderers = AttachmentRendererFactories.reload()

		BlockEntityRendererFactories.register(RoadworksRegistry.ModBlockEntities.POST_CONTAINER_BLOCK_ENTITY, ::PostContainerRenderer)
		BlockEntityRendererFactories.register(RoadworksRegistry.ModBlockEntities.WALL_CONTAINER_BLOCK_ENTITY, ::WallContainerRenderer)

		HandledScreens.register(RoadworksRegistry.ModScreens.SIGN_EDITOR_SCREEN_HANDLER, ::SignEditorScreen)

		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
			RoadworksRegistry.ModBlocks.WHITE_CENTER_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_ARROW_LEFT_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_ARROW_STRAIGHT_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_ARROW_RIGHT_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_ONLY_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_HOV_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_RAILROAD_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_ARROW_LEFT_STRAIGHT_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_ARROW_RIGHT_STRAIGHT_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_ARROW_RIGHT_LEFT_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_ARROW_U_TURN_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_ZEBRA_CROSSING_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_YIELD_MARKING,

			RoadworksRegistry.ModBlocks.WHITE_CENTER_DASH_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_INFILL_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_CENTER_TURN_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_CENTER_THICK,
			RoadworksRegistry.ModBlocks.WHITE_CENTER_STUB_SHORT,
			RoadworksRegistry.ModBlocks.WHITE_CENTER_STUB_MEDIUM,
			RoadworksRegistry.ModBlocks.WHITE_CENTER_STUB_LONG,

			RoadworksRegistry.ModBlocks.WHITE_EDGE_TURN_MARKING_INSIDE,
			RoadworksRegistry.ModBlocks.WHITE_EDGE_TURN_MARKING_OUTSIDE,
			RoadworksRegistry.ModBlocks.WHITE_EDGE_DASH_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_EDGE_MARKING,
			RoadworksRegistry.ModBlocks.WHITE_EDGE_THICK,
			RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_SHORT_LEFT,
			RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_MEDIUM_LEFT,
			RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_LONG_LEFT,
			RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_SHORT_RIGHT,
			RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_MEDIUM_RIGHT,
			RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_LONG_RIGHT,

			RoadworksRegistry.ModBlocks.WHITE_T_CENTER_LONG,
			RoadworksRegistry.ModBlocks.WHITE_T_LEFT_LONG,
			RoadworksRegistry.ModBlocks.WHITE_T_RIGHT_LONG,

			RoadworksRegistry.ModBlocks.WHITE_T_CENTER,
			RoadworksRegistry.ModBlocks.WHITE_T_CENTER_LEFT,
			RoadworksRegistry.ModBlocks.WHITE_T_CENTER_RIGHT,

			RoadworksRegistry.ModBlocks.WHITE_T_CENTER_SHORT,
			RoadworksRegistry.ModBlocks.WHITE_T_SHORT_LEFT,
			RoadworksRegistry.ModBlocks.WHITE_T_SHORT_RIGHT,

			RoadworksRegistry.ModBlocks.WHITE_L_THIN_LEFT,
			RoadworksRegistry.ModBlocks.WHITE_L_THIN_RIGHT,
			RoadworksRegistry.ModBlocks.WHITE_L_LEFT,
			RoadworksRegistry.ModBlocks.WHITE_L_RIGHT,
			RoadworksRegistry.ModBlocks.WHITE_L_SHORT_LEFT,
			RoadworksRegistry.ModBlocks.WHITE_L_SHORT_RIGHT,

			RoadworksRegistry.ModBlocks.YELLOW_CENTER_MARKING,
			RoadworksRegistry.ModBlocks.YELLOW_CENTER_OFFSET,
			RoadworksRegistry.ModBlocks.YELLOW_DOUBLE,
			RoadworksRegistry.ModBlocks.YELLOW_DOUBLE_TURN,
			RoadworksRegistry.ModBlocks.YELLOW_DOUBLE_SPLIT_LEFT,
			RoadworksRegistry.ModBlocks.YELLOW_DOUBLE_SPLIT_RIGHT,

			RoadworksRegistry.ModBlocks.YELLOW_CENTER_OFFSET_INSIDE,
			RoadworksRegistry.ModBlocks.YELLOW_CENTER_OFFSET_OUTSIDE,
			RoadworksRegistry.ModBlocks.YELLOW_OFFSET_OUTSIDE_TO_CENTER_R,
			RoadworksRegistry.ModBlocks.YELLOW_OFFSET_OUTSIDE_TO_CENTER_L,
			RoadworksRegistry.ModBlocks.YELLOW_OFFSET_INSIDE_TO_CENTER_R,
			RoadworksRegistry.ModBlocks.YELLOW_OFFSET_INSIDE_TO_CENTER_L,

			RoadworksRegistry.ModBlocks.YELLOW_CENTER_DASH_MARKING,
			RoadworksRegistry.ModBlocks.YELLOW_INFILL_MARKING,
			RoadworksRegistry.ModBlocks.YELLOW_CENTER_TURN_MARKING,
			RoadworksRegistry.ModBlocks.YELLOW_CENTER_STUB_SHORT,
			RoadworksRegistry.ModBlocks.YELLOW_CENTER_STUB_MEDIUM,
			RoadworksRegistry.ModBlocks.YELLOW_CENTER_STUB_LONG,

			RoadworksRegistry.ModBlocks.YELLOW_EDGE_TURN_MARKING_INSIDE,
			RoadworksRegistry.ModBlocks.YELLOW_EDGE_TURN_MARKING_OUTSIDE,
			RoadworksRegistry.ModBlocks.YELLOW_EDGE_DASH_MARKING,
			RoadworksRegistry.ModBlocks.YELLOW_EDGE_MARKING,
			RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_SHORT_LEFT,
			RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_MEDIUM_LEFT,
			RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_LONG_LEFT,
			RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_SHORT_RIGHT,
			RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_MEDIUM_RIGHT,
			RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_LONG_RIGHT,

			RoadworksRegistry.ModBlocks.YELLOW_T_CENTER_LONG,
			RoadworksRegistry.ModBlocks.YELLOW_T_LEFT_LONG,
			RoadworksRegistry.ModBlocks.YELLOW_T_RIGHT_LONG,

			RoadworksRegistry.ModBlocks.YELLOW_T_CENTER,
			RoadworksRegistry.ModBlocks.YELLOW_T_CENTER_LEFT,
			RoadworksRegistry.ModBlocks.YELLOW_T_CENTER_RIGHT,

			RoadworksRegistry.ModBlocks.YELLOW_T_CENTER_SHORT,
			RoadworksRegistry.ModBlocks.YELLOW_T_SHORT_LEFT,
			RoadworksRegistry.ModBlocks.YELLOW_T_SHORT_RIGHT,

			RoadworksRegistry.ModBlocks.YELLOW_L_THIN_LEFT,
			RoadworksRegistry.ModBlocks.YELLOW_L_THIN_RIGHT,
			RoadworksRegistry.ModBlocks.YELLOW_L_LEFT,
			RoadworksRegistry.ModBlocks.YELLOW_L_RIGHT,
			RoadworksRegistry.ModBlocks.YELLOW_L_SHORT_LEFT,
			RoadworksRegistry.ModBlocks.YELLOW_L_SHORT_RIGHT,

			RoadworksRegistry.ModBlocks.WALL_CONTAINER,
			RoadworksRegistry.ModBlocks.CATWALK
		)

		ModelLoader()
	}
}