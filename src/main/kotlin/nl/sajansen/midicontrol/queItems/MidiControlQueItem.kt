package nl.sajansen.midicontrol.queItems

import nl.sajansen.midicontrol.*
import nl.sajansen.midicontrol.midi.ByteMidiMessage
import objects.notifications.Notifications
import objects.que.JsonQueue
import objects.que.QueItem
import java.awt.Color
import java.util.logging.Logger
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem

class MidiControlQueItem(
    override val plugin: MidiControlPlugin,
    override val name: String,
    private val messages: List<MidiMessage>
) : QueItem {

    private val logger = Logger.getLogger(MidiControlQueItem::class.java.name)

    override var executeAfterPrevious: Boolean = false
    override var quickAccessColor: Color? = plugin.quickAccessColor

    companion object {
        fun fromJson(plugin: MidiControlPlugin, jsonQueItem: JsonQueue.QueueItem): MidiControlQueItem {
            val messages: List<MidiMessage> = jsonQueItem.data["commands"]!!.split(";")
                .map { stringCommands ->
                    val byteArrayCommands = configStringToByteArray(stringCommands)
                    ByteMidiMessage(byteArrayCommands)
                }.toList()
            return MidiControlQueItem(plugin, jsonQueItem.name, messages)
        }
    }

    override fun activate() {
        if (!plugin.isConnected()) {
            logger.info("Midi device not connected; cannot activate midi message")
            Notifications.add("Cannot activate queue item: MIDI device is not connected", "Midi Control")
            return
        }

        messages.forEach {
            logger.info("Sending MIDI command: ${byteArrayStringToConfigString(byteArrayToByteArrayString(it.message))}")
            MidiSystem.getReceiver().send(it, -1)
        }
    }

    override fun deactivate() {}

    override fun toConfigString(): String {
        throw NotImplementedError("This method is deprecated")
    }

    override fun toJson(): JsonQueue.QueueItem {
        val jsonItem = super.toJson()
        jsonItem.data["commands"] = messages
            .map {
                byteArrayStringToConfigString(
                    byteArrayToByteArrayString(it.message)
                )
            }
            .joinToString(";")
        return jsonItem
    }
}