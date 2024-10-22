package me.znepb.roadworks.network

import com.mojang.serialization.*
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.block.sign.custom.CustomSignBlockEntity
import me.znepb.roadworks.util.Charset
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.registry.RegistryKey
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.stream.Stream

data class EditSignPacket(val blockPos: BlockPos, val characters: List<Charset>) {
    companion object {
        val UPDATE_SIGN_PACKET_ID = RoadworksMain.ModId("update_sign_packet")

        private val MAP_CODEC = RecordCodecBuilder.mapCodec<EditSignPacket> { it.group(
            BlockPos.CODEC.fieldOf("blockPos").forGetter(EditSignPacket::blockPos),
            Charset.ARRAY_CODEC.fieldOf("characters").forGetter(EditSignPacket::characters)
        ).apply(it, ::EditSignPacket) }

        val CODEC: Codec<EditSignPacket> = MapCodec.MapCodecCodec(object : MapCodec<EditSignPacket>() {
            override fun <T> keys(ops: DynamicOps<T>): Stream<T> {
                return MAP_CODEC.keys(ops)
            }

            override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<EditSignPacket> {
                return MAP_CODEC.decode(ops, input)
            }

            override fun <T> encode(
                input: EditSignPacket?,
                ops: DynamicOps<T>,
                prefix: RecordBuilder<T>
            ): RecordBuilder<T>? {
                return MAP_CODEC.encode(input, ops, prefix)
            }
        })

        fun register() {
            ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_PACKET_ID) { server, player, _, buf, _ ->
                val signData = buf.decodeAsJson(CODEC)

                server.execute {
                    val blockPos = signData.blockPos
                    val world = player.world
                    val blockEntity = world?.let {
                        val be = it.getBlockEntity(signData.blockPos)
                        if(be is CustomSignBlockEntity) be else null
                    }

                    if(!blockPos.isWithinDistance(player.blockPos, 32.0) || blockEntity == null) {
                        return@execute
                    }

                    blockEntity.contents = signData.characters
                }
            }
        }
    }
}