package me.znepb.roadworks.item

import me.znepb.roadworks.Registry.ModScreens.SIGN_EDITOR_SCREEN_HANDLER
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.math.BlockPos

class SignEditorScreenHandler(syncId: Int, playerInventory: PlayerInventory, inventory: Inventory)
    : ScreenHandler(SIGN_EDITOR_SCREEN_HANDLER, syncId) {

    val propertyDelegate: PropertyDelegate = ArrayPropertyDelegate(3) // x, y, z

    init {
        this.addProperties(propertyDelegate)
    }

    constructor(syncId: Int, playerInventory: PlayerInventory) :
        this(syncId, playerInventory, SimpleInventory(0))

    fun setBlockPosition(pos: BlockPos) {
        propertyDelegate.set(0, pos.x)
        propertyDelegate.set(1, pos.y)
        propertyDelegate.set(2, pos.z)
    }

    fun getBlockPosition(): BlockPos {
        return BlockPos(propertyDelegate.get(0), propertyDelegate.get(1), propertyDelegate.get(2))
    }

    override fun canUse(player: PlayerEntity?) = true

    override fun quickMove(player: PlayerEntity?, slot: Int) = ItemStack.EMPTY
}