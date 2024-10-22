package me.znepb.roadworks

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry

@Config(name = "roadworks")
class Config : ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    var trafficCabinet = TrafficCabinetConfiguration()

    class TrafficCabinetConfiguration {
        var maxDevices: Int = 24
        var maxLinkDistance: Double = 48.0
    }
}