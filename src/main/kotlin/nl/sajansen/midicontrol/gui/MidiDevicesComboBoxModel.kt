package nl.sajansen.midicontrol.gui

import nl.sajansen.midicontrol.MidiDeviceClass
import javax.swing.DefaultComboBoxModel

class MidiDevicesComboBoxModel(items: Array<MidiDeviceClass>) : DefaultComboBoxModel<MidiDeviceClass>(items) {
    override fun setSelectedItem(item: Any) {
        if (item is MidiDeviceClass && item.device?.maxTransmitters == 0) {
            return
        }

        super.setSelectedItem(item)
    }
}