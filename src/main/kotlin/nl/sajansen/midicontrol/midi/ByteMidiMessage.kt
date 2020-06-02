package nl.sajansen.midicontrol.midi

import javax.sound.midi.MidiMessage

class ByteMidiMessage(byteArray: ByteArray) : MidiMessage(byteArray) {
    override fun clone(): Any {
        val byteArray = ByteArray(length)
        System.arraycopy(data, 0, byteArray, 0, byteArray.size)
        return ByteMidiMessage(byteArray)
    }
}