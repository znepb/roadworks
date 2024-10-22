package me.znepb.roadworks.network

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf

class EditSignPacketClient {
    companion object {
        fun sendUpdateSignPacket(packet: EditSignPacket) {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.encodeAsJson(EditSignPacket.CODEC, packet)

            // Send the packet to the server
            MinecraftClient.getInstance().networkHandler?.sendPacket(
                ClientPlayNetworking.createC2SPacket(EditSignPacket.UPDATE_SIGN_PACKET_ID, buf)
            )
        }
    }
}