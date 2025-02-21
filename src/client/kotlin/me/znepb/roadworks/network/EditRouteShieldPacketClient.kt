package me.znepb.roadworks.network

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf

class EditRouteShieldPacketClient {
    companion object {
        fun sendUpdateRouteShieldPacket(packet: EditRouteShieldPacket) {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.encodeAsJson(EditRouteShieldPacket.CODEC, packet)

            // Send the packet to the server
            MinecraftClient.getInstance().networkHandler?.sendPacket(
                ClientPlayNetworking.createC2SPacket(EditRouteShieldPacket.UPDATE_ROUTE_SHIELD_PACKET_ID, buf)
            )
        }
    }
}