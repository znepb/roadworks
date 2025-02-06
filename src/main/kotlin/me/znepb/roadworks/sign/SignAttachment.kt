package me.znepb.roadworks.sign

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.RoadworksRegistry.ModAttachments.SIGN_ATTACHMENT
import me.znepb.roadworks.attachment.PositionableAttachment
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.util.RotateVoxelShape.Companion.offsetFromDirectionXZ
import me.znepb.roadworks.util.RotateVoxelShape.Companion.rotateVoxelShape
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.joml.Vector3d

class SignAttachment(
    container: AttachmentContainerBlockEntity,
) : PositionableAttachment(SIGN_ATTACHMENT, container) {
    var signType = ModId("unknown")

    val signFixes = mutableMapOf<Identifier, Identifier>().also {
        it[ModId("4_way")] = ModId("supplemental_all_way")
        it[ModId("ahead")] = ModId("supplemental_ahead")
    }

    companion object {
        val SIGN_SHAPE_WALL = BlockWithEntity.createCuboidShape(0.0, 0.0, 7.75, 16.0, 16.0, 8.25)
    }

    fun getSignData() = RoadworksMain.signageManager.getSign(signType)

    override fun readNBT(nbt: NbtCompound) {
        super.readNBT(nbt)

        val newSignType = Identifier.tryParse(nbt.getString("sign_type"))
        if(newSignType == null) {
            logger.warn("Invalid sign identifier: {}", nbt.getString(("sign_type")))
        } else {
            signType = if(signFixes.contains(newSignType)) {
                logger.info("Fixed sign type $newSignType to ${signFixes[newSignType]!!}")
                signFixes[newSignType]!!
            } else {
                newSignType
            }
        }
    }

    override fun writeNBT(nbt: NbtCompound) {
        nbt.putString("sign_type", signType.toString())
        super.writeNBT(nbt)
    }

    override fun getShape(context: ShapeContext): VoxelShape {
        val depthOffset = this.container.getDepthOffset()

        return offsetFromDirectionXZ(
            rotateVoxelShape(SIGN_SHAPE_WALL, Direction.NORTH, this.facing),
            facing,
            Vector3d(0.0, 0.0, -depthOffset),
            Vector3d(depthOffset, 0.0, 0.0,),
            Vector3d(0.0, 0.0, depthOffset),
            Vector3d(-depthOffset, 0.0, 0.0),
        )
    }

    override fun isPositionable() = if(this.getSignData() != null) this.getSignData()!!.height!! < 64 else true
}