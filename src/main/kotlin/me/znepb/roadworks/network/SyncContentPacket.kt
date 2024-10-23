package me.znepb.roadworks.network

import com.mojang.serialization.*
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.block.sign.CustomSignBlockEntity
import me.znepb.roadworks.block.sign.SignType
import me.znepb.roadworks.block.sign.SignTypeWithIdentifier
import me.znepb.roadworks.util.Charset
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import java.util.stream.Stream

data class SyncContentPacket(val signs: List<SignTypeWithIdentifier>) {
    companion object {
        val SYNC_CONTENT_PACKET_ID = RoadworksMain.ModId("sync_content")

        private val MAP_CODEC = RecordCodecBuilder.mapCodec<SyncContentPacket>{ it.group(
            SignTypeWithIdentifier.CODEC.listOf().fieldOf("signs").forGetter(SyncContentPacket::signs)
        ).apply(it, ::SyncContentPacket) }

        val CODEC: Codec<SyncContentPacket> = MapCodec.MapCodecCodec(object : MapCodec<SyncContentPacket>() {
            override fun <T> keys(ops: DynamicOps<T>): Stream<T> {
                return MAP_CODEC.keys(ops)
            }

            override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<SyncContentPacket> {
                return MAP_CODEC.decode(ops, input)
            }

            override fun <T> encode(
                input: SyncContentPacket?,
                ops: DynamicOps<T>,
                prefix: RecordBuilder<T>
            ): RecordBuilder<T>? {
                return MAP_CODEC.encode(input, ops, prefix)
            }
        })
    }
}