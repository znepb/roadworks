package me.znepb.roadworks.util

import net.minecraft.data.client.BlockStateVariant
import net.minecraft.data.client.MultipartBlockStateSupplier
import net.minecraft.data.client.VariantSettings
import net.minecraft.data.client.When
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

class OrientedBlockStateSupplier(
    model: Identifier,
    whenSet: (When.PropertyCondition) -> When = { it },
    blockStateSet: (BlockStateVariant) -> BlockStateVariant = { it },
    turnOffset: Int = 0,
) {
    fun put(supplier: MultipartBlockStateSupplier): MultipartBlockStateSupplier {
        supplier.with(whenNorth, stateNorth)
        supplier.with(whenEast, stateEast)
        supplier.with(whenSouth, stateSouth)
        supplier.with(whenWest, stateWest)
        return supplier
    }

    val whenNorth = whenSet(When.create().set(Properties.HORIZONTAL_FACING, Direction.NORTH))
    val stateNorth = blockStateSet(
        BlockStateVariant.create()
            .put(VariantSettings.Y,
                when(turnOffset) {
                    1 -> VariantSettings.Rotation.R90
                    2 -> VariantSettings.Rotation.R180
                    3 -> VariantSettings.Rotation.R270
                    else -> VariantSettings.Rotation.R0
                })
            .put(VariantSettings.MODEL, model)
    )

    val whenEast = whenSet(When.create().set(Properties.HORIZONTAL_FACING, Direction.EAST))
    val stateEast = blockStateSet(
        BlockStateVariant.create()
            .put(VariantSettings.Y, when(turnOffset) {
                1 -> VariantSettings.Rotation.R180
                2 -> VariantSettings.Rotation.R270
                3 -> VariantSettings.Rotation.R0
                else -> VariantSettings.Rotation.R90
            })
            .put(VariantSettings.MODEL, model)
    )

    val whenSouth = whenSet(When.create().set(Properties.HORIZONTAL_FACING, Direction.SOUTH))
    val stateSouth = blockStateSet(
        BlockStateVariant.create()
            .put(VariantSettings.Y, when(turnOffset) {
                1 -> VariantSettings.Rotation.R270
                2 -> VariantSettings.Rotation.R0
                3 -> VariantSettings.Rotation.R90
                else -> VariantSettings.Rotation.R180
            })
            .put(VariantSettings.MODEL, model)
    )


    val whenWest = whenSet(When.create().set(Properties.HORIZONTAL_FACING, Direction.WEST))
    val stateWest = blockStateSet(
        BlockStateVariant.create()
            .put(VariantSettings.Y, when(turnOffset) {
                1 -> VariantSettings.Rotation.R0
                2 -> VariantSettings.Rotation.R90
                3 -> VariantSettings.Rotation.R180
                else -> VariantSettings.Rotation.R270
            })
            .put(VariantSettings.MODEL, model)
    )
}