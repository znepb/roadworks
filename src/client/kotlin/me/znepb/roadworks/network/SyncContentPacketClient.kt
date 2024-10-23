package me.znepb.roadworks.network

import me.znepb.roadworks.RoadworksClient.logger
import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.network.SyncContentPacket.Companion.SYNC_CONTENT_PACKET_ID
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

class SyncContentPacketClient {
    companion object {
        fun register() {
            ClientPlayNetworking.registerGlobalReceiver(SYNC_CONTENT_PACKET_ID) { client, handler, buf, response ->
                val signData = buf.decodeAsJson(SyncContentPacket.CODEC)

                client.execute {
                    logger.debug("Syncing server signage")

                    RoadworksMain.SIGN_TYPES.clear()
                    signData.signs.forEach {
                        RoadworksMain.SIGN_TYPES[it.identifier] = it.signType
                    }

                    logger.info("Server signage synced")
                }
            }
        }
    }
}