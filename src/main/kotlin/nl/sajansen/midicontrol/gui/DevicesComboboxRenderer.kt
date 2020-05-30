package nl.sajansen.midicontrol.gui

import themes.Theme
import java.awt.Component
import java.awt.Font
import javax.sound.midi.MidiDevice
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.plaf.basic.BasicComboBoxRenderer

class DevicesComboboxRenderer : BasicComboBoxRenderer() {
    override fun getListCellRendererComponent(list: JList<*>, value: Any?, index: Int, isSelected: Boolean, hasFocus: Boolean): Component {
        val cell = super.getListCellRendererComponent(list, value, index, isSelected, hasFocus) as JLabel
        if (value == null) {
            return cell
        }

        val deviceInfo = value as MidiDevice.Info
        cell.text = "${deviceInfo.name} (${deviceInfo.description})"
        cell.font = Font(Theme.get.FONT_FAMILY, Font.PLAIN, 12)
        return cell
    }
}