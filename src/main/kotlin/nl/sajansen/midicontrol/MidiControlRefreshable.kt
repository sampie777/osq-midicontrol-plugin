package nl.sajansen.midicontrol

interface MidiControlRefreshable {
    fun onMidiControlEnabled() {}
    fun onMidiControlDisabled() {}

    fun onMidiDeviceConnected() {}
    fun onMidiDeviceDisconnected() {}

    fun onMidiCommandReceived(command: String) {}
    fun onMidiDevicesUpdated() {}

    fun onCalibrated(key: String, value: String) {}
}