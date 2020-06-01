package nl.sajansen.midicontrol.gui

import nl.sajansen.midicontrol.*
import objects.notifications.Notifications
import themes.Theme
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.util.logging.Logger
import javax.sound.midi.MidiDevice
import javax.swing.*

class MidiConnectionPanel(private val plugin: MidiControlPlugin) : JPanel(), MidiControlRefreshable {
    private val logger = Logger.getLogger(MidiConnectionPanel::class.java.name)

    private val devicesComboBox = JComboBox<MidiDeviceClass>()
    private val connectButton = JButton()

    init {
        initGui()

        refreshDevicesComboBox()
        setDevicesComboBoxToCurrentDevice()
        refreshConnectButtonText()

        MidiControlRefreshableRegister.register(this)
    }

    private fun initGui() {
        layout = BorderLayout(10, 10)
        alignmentX = Component.LEFT_ALIGNMENT

        add(devicesComboBox, BorderLayout.CENTER)
        devicesComboBox.border = BorderFactory.createLineBorder(Theme.get.BORDER_COLOR)
        devicesComboBox.renderer = DevicesComboboxRenderer()
        devicesComboBox.preferredSize = Dimension(200, 20)

        add(connectButton, BorderLayout.LINE_END)
        connectButton.addActionListener { toggleMidiConnection() }
    }

    private fun refreshDevicesComboBox() {
        devicesComboBox.model = MidiDevicesComboBoxModel(plugin.allMidiDevices.toTypedArray())
    }

    private fun refreshConnectButtonText() {
        connectButton.text =
            if (plugin.isConnected()) "Disconnect" else "Connect"
    }

    private fun toggleMidiConnection() {
        if (plugin.isConnected()) {
            connectButton.text = "Disconnecting..."
            plugin.disconnectMidiDevice()
        } else {
            val selectedDevice = devicesComboBox.selectedItem
            if (selectedDevice == null) {
                logger.info("No midi device selected")
                Notifications.add("First select a Midi device to connect with", "Midi Control")
                return
            }

            connectButton.text = "Connecting..."
            plugin.connectMidiDevice(selectedDevice as MidiDeviceClass)
        }
    }

    private fun setDevicesComboBoxToCurrentDevice() {
        val selectedItem: MidiDeviceClass? = plugin.activeMidiDevice()
                ?: plugin.allMidiDevices.find { it.id == MidiControlProperties.midiDeviceIdentifier }
                ?: plugin.allMidiDevices.first { it.device?.maxTransmitters != 0 }

        devicesComboBox.selectedItem = selectedItem
    }

    override fun onMidiDeviceConnected() {
        setDevicesComboBoxToCurrentDevice()
        refreshConnectButtonText()
    }

    override fun onMidiDeviceDisconnected() {
        refreshConnectButtonText()
    }

    override fun onMidiDevicesUpdated() {
        refreshDevicesComboBox()
    }

}