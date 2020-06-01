package nl.sajansen.midicontrol.gui

import nl.sajansen.midicontrol.MidiDeviceClass
import nl.sajansen.midicontrol.deviceInfoToString
import themes.Theme
import java.awt.Component
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.plaf.basic.BasicComboBoxRenderer

class DevicesComboboxRenderer : BasicComboBoxRenderer() {
    override fun getListCellRendererComponent(list: JList<*>, value: Any?, index: Int, isSelected: Boolean, hasFocus: Boolean): Component {
        val cell = super.getListCellRendererComponent(list, value, index, isSelected, hasFocus) as JLabel
        if (value == null) {
            return cell
        }

        val midiDevice = value as MidiDeviceClass
        cell.text = deviceInfoToString(midiDevice.info)

        if (midiDevice.device?.maxTransmitters == 0) {
            cell.font = Font(Theme.get.FONT_FAMILY, Font.ITALIC, 11)
            cell.text = "(not compatible) ${cell.text}"
        } else {
            cell.font = Font(Theme.get.FONT_FAMILY, Font.PLAIN, 12)
        }
        return cell
    }
}