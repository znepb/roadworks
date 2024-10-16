package me.znepb.roadworks.block.sign

import com.mojang.serialization.*
import com.mojang.serialization.MapCodec.MapCodecCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Identifier
import java.util.stream.Stream

data class SignType(val name: String, val frontTexture: Identifier, val backTexture: Identifier) {
    companion object {
        // IntelIJ says this <SignType> generic is redundant - Kotlin WILL NOT BUILD without it as of 10/14/2024.
        // Unless this gets resolved in the future, do not remove, either anyone or future me!

        val CODEC_V1 = RecordCodecBuilder.mapCodec<SignType> { it.group(
                Codec.STRING.fieldOf("name").forGetter(SignType::name),
                Identifier.CODEC.fieldOf("back_texture").forGetter(SignType::backTexture),
                Identifier.CODEC.fieldOf("front_texture").forGetter(SignType::frontTexture)
                ).apply(it, ::SignType) }

        val CODEC: Codec<SignType> = MapCodecCodec(object : MapCodec<SignType>() {
            override fun <T> keys(ops: DynamicOps<T>): Stream<T> {
                return CODEC_V1.keys(ops)
            }

            override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<SignType> {
                return CODEC_V1.decode(ops, input)
            }

            override fun <T> encode(input: SignType?, ops: DynamicOps<T>, prefix: RecordBuilder<T>): RecordBuilder<T>? {
                return CODEC_V1.encode(input, ops, prefix)
            }
        })
    }
}