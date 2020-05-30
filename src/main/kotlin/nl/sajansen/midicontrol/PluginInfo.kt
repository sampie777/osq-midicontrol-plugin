package nl.sajansen.midicontrol

import java.util.*

object PluginInfo {
    private val properties = Properties()
    val version: String
    val author: String

    init {
        properties.load(MidiControlPlugin::class.java.getResourceAsStream("/nl/sajansen/midicontrol/plugin.properties"))
        version = properties.getProperty("version")
        author = properties.getProperty("author")
    }
}