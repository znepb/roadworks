package me.znepb.zrm.datagen

import me.znepb.zrm.Main.NAMESPACE
import me.znepb.zrm.Registry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.util.Identifier

class LanguageProvider(output: FabricDataOutput) : FabricLanguageProvider(output) {
    override fun generateTranslations(translationBuilder: TranslationBuilder) {
        translationBuilder.add(Registry.ModBlocks.THICK_POST, "Thick Post")
        translationBuilder.add(Registry.ModBlocks.POST, "Post")
        translationBuilder.add(Registry.ModBlocks.THIN_POST, "Thin Post")

        translationBuilder.add(Registry.ModBlocks.TRAFFIC_CONE, "Traffic Cone")
        translationBuilder.add(Registry.ModBlocks.CHANNELER, "Channeler")
        translationBuilder.add(Registry.ModBlocks.DRUM, "Drum")

        translationBuilder.add(Registry.ModBlocks.STOP_SIGN, "Stop Sign")
        translationBuilder.add(Registry.ModBlocks.STOP_SIGN_4_WAY, "4-way Stop Sign")
        translationBuilder.add(Registry.ModBlocks.STOP_SIGN_AHEAD, "Stop Ahead Sign")
        translationBuilder.add(Registry.ModBlocks.YIELD_SIGN, "Yield Sign")
        translationBuilder.add(Registry.ModBlocks.YIELD_SIGN_AHEAD, "Yield Ahead Sign")
        translationBuilder.add(Registry.ModBlocks.SIGNAL_AHEAD, "Signal Ahead Sign")
        translationBuilder.add(Registry.ModBlocks.ROAD_WORK_AHEAD, "Road Work Ahead Sign")

        translationBuilder.add(Registry.ModItems.LINKER, "Linker")
        translationBuilder.add(Registry.ModBlocks.TRAFFIC_CABINET, "Traffic Cabinet")
        translationBuilder.add(Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL, "Three-head Traffic Signal")

        translationBuilder.add("itemGroup.${NAMESPACE}.main", "znepb's Road Mod")
    }
}