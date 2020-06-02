package nl.sajansen.midicontrol

import kotlin.test.Test
import kotlin.test.assertEquals

class MidiControlPropertiesTest {

    @Test
    fun testByteArrayStringToConfigString() {
        val byteArrayString1 = String(byteArrayOf(-16, 127, 127, 6, 5, -9), MidiControlProperties.charset)
        assertEquals("-16,127,127,6,5,-9", byteArrayStringToConfigString(byteArrayString1))
    }

    @Test
    fun testConfigStringToByteArrayString() {
        val byteArrayString1 = String(byteArrayOf(-16, 127, 127, 6, 5, -9), MidiControlProperties.charset)
        assertEquals(byteArrayString1, configStringToByteArrayString("-16,127,127,6,5,-9"))
    }
}