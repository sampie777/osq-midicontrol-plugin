package nl.sajansen.midicontrol

import javax.sound.midi.MidiDevice

fun deviceInfoToString(deviceInfo: MidiDevice.Info): String {
    return "${deviceInfo.name} (${deviceInfo.description})"
}

fun byteArrayStringToByteArray(text: String): ByteArray {
    return text.toByteArray(MidiControlProperties.charset)
}

fun byteArrayToByteArrayString(byteArray: ByteArray): String {
    return String(byteArray, MidiControlProperties.charset)
}

fun configStringToByteArray(text: String): ByteArray {
    return text.split(",")
        .map {
            it.trim()
                .toInt()
                .toByte()
        }
        .toByteArray()
}

fun configStringToByteArrayString(byteText: String): String {
    if (byteText.isEmpty()) {
        return ""
    }
    return byteArrayToByteArrayString(
        configStringToByteArray(byteText)
    )
}

fun byteArrayStringToConfigString(text: String): String {
    return text.toByteArray(MidiControlProperties.charset)
        .joinToString(",")
}