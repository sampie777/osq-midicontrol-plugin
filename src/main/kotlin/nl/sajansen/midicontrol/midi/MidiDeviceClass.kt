package nl.sajansen.midicontrol.midi

import nl.sajansen.midicontrol.deviceInfoToString
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

data class MidiDeviceClass(val info: MidiDevice.Info) {
    var device: MidiDevice? = MidiSystem.getMidiDevice(info)
    val id = deviceInfoToString(info) + " [${device?.maxTransmitters}][${device?.maxReceivers}]"
}