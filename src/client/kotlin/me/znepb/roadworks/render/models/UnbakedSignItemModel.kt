package me.znepb.roadworks.render.models

import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.Baker
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.Identifier
import java.util.function.Function

class UnbakedSignItemModel : UnbakedModel {
    override fun getModelDependencies(): MutableCollection<Identifier> = mutableListOf()

    override fun setParents(modelLoader: Function<Identifier, UnbakedModel>?) {

    }

    override fun bake(
        baker: Baker,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModel {
        return SignItemBakedModel(textureGetter.apply(SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier("block/stone"))))
    }
}