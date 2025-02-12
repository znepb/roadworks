package me.znepb.roadworks.attachment

import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import net.minecraft.nbt.NbtCompound

abstract class ActivatableAttachment(type: AttachmentType<*>, container: AttachmentContainerBlockEntity) : LinkableAttachment(type, container) {
    private var isActivated = false

    override fun readNBT(nbt: NbtCompound) {
        this.isActivated = nbt.getBoolean("active")
        super.readNBT(nbt)

    }

    override fun writeNBT(nbt: NbtCompound) {
        super.writeNBT(nbt)
        nbt.putBoolean("active", isActivated)
    }

    fun isActive() = isActivated
    fun activate() = setActive(true)
    fun deactivate() = setActive(false)
    open fun setActive(active: Boolean) {
        isActivated = active
        this.markDirty()
    }
}