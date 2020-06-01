package nl.sajansen.midicontrol

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

data class MidiDeviceClass(val info: MidiDevice.Info) {
    var device: MidiDevice? = MidiSystem.getMidiDevice(info)
    val id = deviceInfoToString(info) + " [${device?.maxTransmitters}]"
}