package me.znepb.zrm.datagen

import me.znepb.zrm.Registry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture


class TagProvider(output: FabricDataOutput, completableFuture: CompletableFuture<WrapperLookup>) : BlockTagProvider(output, completableFuture) {
    companion object {
        val POSTS = TagKey.of(RegistryKeys.BLOCK, Identifier("zrm", "posts"))
        val SIGNS = TagKey.of(RegistryKeys.BLOCK, Identifier("zrm", "signs"))
    }

    override fun configure(arg: WrapperLookup) {
        getOrCreateTagBuilder(POSTS)
            .add(Registry.ModBlocks.POST)
            .add(Registry.ModBlocks.THIN_POST)
            .add(Registry.ModBlocks.THICK_POST)

        getOrCreateTagBuilder(SIGNS)
            .add(Registry.ModBlocks.STOP_SIGN)
    }
}

