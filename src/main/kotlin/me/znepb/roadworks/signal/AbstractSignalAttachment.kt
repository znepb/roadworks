package me.znepb.roadworks.signal

import me.znepb.roadworks.attachment.AttachmentType
import me.znepb.roadworks.attachment.LinkableAttachment
import me.znepb.roadworks.container.AttachmentContainerBlockEntity
import me.znepb.roadworks.container.PostContainerBlockEntity
import net.minecraft.nbt.NbtCompound

abstract class AbstractSignalAttachment(
    val signalType: SignalType,
    type: AttachmentType<*>,
    container: AttachmentContainerBlockEntity,
) : LinkableAttachment(type, container) {
    val signalState = signalStateMapFromList(signalType)
    val queue = hashMapOf<SignalLight, Boolean>()

    companion object {
        fun signalStateMapFromList(signalType: SignalType): HashMap<SignalLight, Boolean> {
            val map = hashMapOf<SignalLight, Boolean>()
            signalType.lights.forEach { map[it] = false }

            return map
        }
    }

    init {
        markDirty()
    }

    override fun getLinkType() = this.signalType.type

    fun queueSignalSet(light: SignalLight, value: Boolean) {
        queue[light] = value
    }

    fun isSignalActive(signal: SignalLight): Boolean {
        return signalState[signal] == true
    }

    fun setSignalActive(signal: SignalLight, active: Boolean) {
        if(!signalType.lights.contains(signal)) return
        signalState[signal] = active
        this.markDirty()
    }

    override fun readNBT(nbt: NbtCompound) {
        signalType.lights.forEach {
            signalState[it] = if(nbt.contains(it.light)) nbt.getBoolean(it.light) else false
        }

        super.readNBT(nbt)
    }

    override fun writeNBT(nbt: NbtCompound) {
        signalType.lights.forEach {
            signalState[it]?.let { it1 -> nbt.putBoolean(it.light, it1) }
        }

        super.writeNBT(nbt)
    }

    override fun onTick() {
        val world = this.container.world

        val server = world?.server
        if(world?.isClient == false && server != null && !this.linked) {
            val tick = server.ticks
            if(this.signalType.lights.size == 1) {
                queueSignalSet(this.signalType.lights[0], (tick % 40) > 20)
            } else {
                signalType.lights.forEach {
                    if(it.genericType == SignalLight.RED || (it.isGeneric && it == SignalLight.RED)) {
                        queueSignalSet(it, (tick % 40) > 20)
                    } else {
                        queueSignalSet(it, false)
                    }
                }
            }
        }

        if(queue.size > 0) {
            signalType.lights.forEach { signal ->
                if(this.signalState[signal] != queue[signal]) {
                    queue[signal]?.let { value ->
                        this.setSignalActive(signal, value)
                        this.markDirty()
                    }
                }
            }
        }
    }
}