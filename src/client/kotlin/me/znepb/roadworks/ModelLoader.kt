package me.znepb.roadworks

import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.render.models.UnbakedSignItemModel
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier.OnLoad
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.util.ModelIdentifier

class ModelLoader : ModelLoadingPlugin {
    override fun onInitializeModelLoader(pluginContext: ModelLoadingPlugin.Context) {
        // We want to add our model when the models are loaded
        pluginContext.modifyModelOnLoad().register(OnLoad { original: UnbakedModel?, context: OnLoad.Context ->
            // This is called for every model that is loaded, so make sure we only target ours
            val id = context.id()
            if (id != null && id.equals(SIGN_MODEL)) {
                UnbakedSignItemModel()
            } else {
                // If we don't modify the model we just return the original as-is
                original
            }
        })
    }

    companion object {
        val SIGN_MODEL: ModelIdentifier =
            ModelIdentifier(ModId("sign"), "inventory")
    }
}