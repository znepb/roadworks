package me.znepb.roadworks.item

import com.mojang.serialization.*
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.Unpooled
import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksRegistry.ModScreens.ROUTE_SHIELD_EDITOR
import me.znepb.roadworks.RoadworksRegistry.ModScreens.SIGN_EDITOR_SCREEN_HANDLER
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Uuids
import net.minecraft.util.math.BlockPos
import java.util.*
import java.util.stream.Stream

abstract class AbstractSignEditorScreenHandler(type: ScreenHandlerType<*>, syncId: Int, playerInventory: PlayerInventory)
    : ScreenHandler(type, syncId) {
    private var pos: BlockPos? = null
    private var uuid: UUID? = null

    init {
        pos = BlockPos.ORIGIN
        uuid = UUID.randomUUID()
    }

    fun setBlockPosition(pos: BlockPos) {
        this.pos = pos
    }

    fun setAttachmentUUID(uuid: UUID) {
        this.uuid = uuid
    }

    fun getBlockPosition() = this.pos
    fun getAttachmentUUID() = this.uuid

    override fun canUse(player: PlayerEntity?) = true

    override fun quickMove(player: PlayerEntity?, slot: Int) = ItemStack.EMPTY

    companion object {
        fun sendDataToClient(player: ServerPlayerEntity, data: SyncData) {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.encodeAsJson(SyncData.CODEC, data)
            ServerPlayNetworking.send(player, SyncData.PACKET_ID, buf)
        }
    }

    data class SyncData(val pos: BlockPos, val uuid: UUID) {
        companion object {
            val PACKET_ID = RoadworksMain.ModId("sync_sign_editor_information")

            private val MAP_CODEC = RecordCodecBuilder.mapCodec<SyncData>{ it.group(
                BlockPos.CODEC.fieldOf("blockPos").forGetter(SyncData::pos),
                Uuids.CODEC.fieldOf("attachmentID").forGetter(SyncData::uuid)
            ).apply(it, ::SyncData) }

            val CODEC: Codec<SyncData> = MapCodec.MapCodecCodec(object : MapCodec<SyncData>() {
                override fun <T> keys(ops: DynamicOps<T>): Stream<T> {
                    return MAP_CODEC.keys(ops)
                }

                override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<SyncData> {
                    return MAP_CODEC.decode(ops, input)
                }

                override fun <T> encode(
                    input: SyncData?,
                    ops: DynamicOps<T>,
                    prefix: RecordBuilder<T>
                ): RecordBuilder<T>? {
                    return MAP_CODEC.encode(input, ops, prefix)
                }
            })
        }
    }

    class RoadSignEditorScreenHandler(syncId: Int, playerInventory: PlayerInventory) : AbstractSignEditorScreenHandler(SIGN_EDITOR_SCREEN_HANDLER, syncId, playerInventory)
    class RouteShieldEditorScreenHandler(syncId: Int, playerInventory: PlayerInventory) : AbstractSignEditorScreenHandler(ROUTE_SHIELD_EDITOR, syncId, playerInventory)
}