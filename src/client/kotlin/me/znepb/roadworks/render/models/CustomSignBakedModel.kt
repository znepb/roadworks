package me.znepb.roadworks.render.models

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.block.sign.custom.CustomSignBlockEntity
import me.znepb.roadworks.render.SignBlockRenderer.Companion.POST_SIGN_NONE_FRONT
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView.BAKE_LOCK_UV
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView.BAKE_ROTATE_NONE
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.screen.PlayerScreenHandler.BLOCK_ATLAS_TEXTURE
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.json.ModelOverrideList
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.item.BlockItem.getBlockEntityNbt
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockRenderView
import java.util.*
import java.util.function.Supplier
import kotlin.math.sign

class CustomSignBakedModel(val sprite: Sprite) : BakedModel, FabricBakedModel {
    private val minecraft by lazy {MinecraftClient.getInstance()}
    private val meshBuilder = ThreadLocal.withInitial { RendererAccess.INSTANCE.renderer?.meshBuilder() }

    override fun isVanillaAdapter() = false

    override fun emitBlockQuads(
        blockView: BlockRenderView,
        state: BlockState,
        pos: BlockPos,
        randomSupplier: Supplier<Random>,
        context: RenderContext
    ) {

    }

    override fun emitItemQuads(stack: ItemStack, randomSupplier: Supplier<Random>?, context: RenderContext) {
        val type = getBlockEntityNbt(stack)?.getString("sign_type")

        if(type != null && RoadworksMain.SIGN_TYPES.get(Identifier(type)) != null) {
            val signType = RoadworksMain.SIGN_TYPES[Identifier(type)]!!
            val spriteIdentifier = SpriteIdentifier(BLOCK_ATLAS_TEXTURE, signType.backTexture)
            val sprite = spriteIdentifier.sprite
            val tint = 0xFFFFFFFF.toInt()
            context.emitter.cullFace(null)
            context.emitter
                .square(Direction.NORTH, 0F, 0F, 1F, 1F, 0.5F)
                .spriteBake(sprite, BAKE_LOCK_UV)
                .color(0, tint)
                .color(1, tint)
                .color(2, tint)
                .color(3, tint)
                .emit()
        } else {
            minecraft.bakedModelManager.missingModel.emitItemQuads(stack, randomSupplier, context)
        }
    }

    override fun getQuads(state: BlockState?, face: Direction?, random: Random?): MutableList<BakedQuad> = mutableListOf()

    override fun useAmbientOcclusion() = true
    override fun hasDepth() = false
    override fun isSideLit() = false
    override fun isBuiltin() = false
    override fun getParticleSprite() = sprite
    override fun getTransformation(): ModelTransformation = ModelHelper.MODEL_TRANSFORM_BLOCK
    override fun getOverrides(): ModelOverrideList = ModelOverrideList.EMPTY
}