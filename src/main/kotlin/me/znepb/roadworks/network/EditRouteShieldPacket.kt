package me.znepb.roadworks.network

import com.mojang.serialization.*
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.sign.RoadSignAttachment
import me.znepb.roadworks.sign.RouteShieldAttachment
import me.znepb.roadworks.util.Charset
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.Uuids
import net.minecraft.util.math.BlockPos
import java.util.*
import java.util.stream.Stream

data class EditRouteShieldPacket(val blockPos: BlockPos, val attachmentId: UUID, val number: Int) {
    companion object {
        val UPDATE_ROUTE_SHIELD_PACKET_ID = RoadworksMain.ModId("update_route_shield_packet")

        private val MAP_CODEC = RecordCodecBuilder.mapCodec<EditRouteShieldPacket>{ it.group(
            BlockPos.CODEC.fieldOf("blockPos").forGetter(EditRouteShieldPacket::blockPos),
            Uuids.CODEC.fieldOf("attachmentID").forGetter(EditRouteShieldPacket::attachmentId),
            Codec.INT.fieldOf("number").forGetter(EditRouteShieldPacket::number)
        ).apply(it, ::EditRouteShieldPacket) }

        val CODEC: Codec<EditRouteShieldPacket> = MapCodec.MapCodecCodec(object : MapCodec<EditRouteShieldPacket>() {
            override fun <T> keys(ops: DynamicOps<T>): Stream<T> {
                return MAP_CODEC.keys(ops)
            }

            override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<EditRouteShieldPacket> {
                return MAP_CODEC.decode(ops, input)
            }

            override fun <T> encode(
                input: EditRouteShieldPacket?,
                ops: DynamicOps<T>,
                prefix: RecordBuilder<T>
            ): RecordBuilder<T>? {
                return MAP_CODEC.encode(input, ops, prefix)
            }
        })

        fun register() {
            ServerPlayNetworking.registerGlobalReceiver(UPDATE_ROUTE_SHIELD_PACKET_ID) { server, player, _, buf, _ ->
                val signData = buf.decodeAsJson(CODEC)

                server.execute {
                    val blockPos = signData.blockPos
                    val world = player.world
                    val blockEntity = world?.let {
                        val be = it.getBlockEntity(signData.blockPos)
                        if(be is AttachmentContainerBlockEntity) be else null
                    }

                    if(!blockPos.isWithinDistance(player.blockPos, 32.0) || blockEntity == null) {
                        return@execute
                    }

                    val attachment = blockEntity.getAttachment(signData.attachmentId)
                    if(attachment !is RouteShieldAttachment) return@execute

                    attachment.number = signData.number
                    attachment.container.markDirty()
                }
            }
        }
    }
}