package me.znepb.roadworks.block.sign

import com.mojang.serialization.*
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Identifier
import java.util.stream.Stream

data class SignTypeWithIdentifier(val identifier: Identifier, val signType: SignType) {
    companion object {
        // IntelIJ says this <SignType> generic is redundant - Kotlin WILL NOT BUILD without it as of 10/14/2024.
        // Unless this gets resolved in the future, do not remove, either anyone or future me!
        val CODEC_V1 = RecordCodecBuilder.mapCodec<SignTypeWithIdentifier> {
            it.group(
                Identifier.CODEC.fieldOf("identifier").forGetter(SignTypeWithIdentifier::identifier),
                SignType.CODEC.fieldOf("signType").forGetter(SignTypeWithIdentifier::signType)
            ).apply(it, ::SignTypeWithIdentifier)
        }

        val CODEC: Codec<SignTypeWithIdentifier> = MapCodec.MapCodecCodec(object : MapCodec<SignTypeWithIdentifier>() {
            override fun <T> keys(ops: DynamicOps<T>): Stream<T> {
                return CODEC_V1.keys(ops)
            }

            override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<SignTypeWithIdentifier> {
                return CODEC_V1.decode(ops, input)
            }

            override fun <T> encode(input: SignTypeWithIdentifier?, ops: DynamicOps<T>, prefix: RecordBuilder<T>): RecordBuilder<T>? {
                return CODEC_V1.encode(input, ops, prefix)
            }
        })
    }
}