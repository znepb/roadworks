package me.znepb.roadworks.attachment

import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import net.minecraft.block.Block

class AttachmentType<T : Attachment>(val factory: AttachmentTypeFactory<out T>) {
    class Builder<T : Attachment>(
        private val factory: AttachmentTypeFactory<out T>,
    ) {
        fun build(): AttachmentType<T> {
            return AttachmentType(factory)
        }

        companion object {
            fun <T : Attachment> create(factory: AttachmentTypeFactory<out T>, vararg blocks: Block): Builder<T> {
                return Builder(factory)
            }
        }
    }

    fun interface AttachmentTypeFactory<T : Attachment> {
        fun create(container: AttachmentContainerBlockEntity): T
    }
}