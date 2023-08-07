package techreborn.client.render

/* From TechReborn - https://github.com/TechReborn/TechReborn/blob/1.20/src/client/java/techreborn/client/render/ModelHelper.java */

import com.google.common.base.Charsets
import me.znepb.zrm.Main.logger
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.util.Identifier
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader

object ModelHelper {
    val DEFAULT_ITEM_TRANSFORMS = loadTransformFromJson(Identifier("minecraft:models/item/generated"))
    val HANDHELD_ITEM_TRANSFORMS = loadTransformFromJson(Identifier("minecraft:models/item/handheld"))
    fun loadTransformFromJson(location: Identifier): ModelTransformation? {
        return try {
            JsonUnbakedModel.deserialize(getReaderForResource(location)).transformations
        } catch (exception: IOException) {
            logger.warn("Can't load resource $location")
            exception.printStackTrace()
            null
        }
    }

    @Throws(IOException::class)
    fun getReaderForResource(location: Identifier): Reader {
        val file = Identifier(location.namespace, location.path + ".json")
        val resource = MinecraftClient.getInstance().resourceManager.getResource(file).orElseThrow()
        return BufferedReader(InputStreamReader(resource.inputStream, Charsets.UTF_8))
    }
}