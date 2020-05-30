package nl.sajansen.midicontrol

import javax.sound.midi.MidiDevice

fun deviceInfoToString(deviceInfo: MidiDevice.Info): String {
    return "${deviceInfo.name} (${deviceInfo.description})"
}