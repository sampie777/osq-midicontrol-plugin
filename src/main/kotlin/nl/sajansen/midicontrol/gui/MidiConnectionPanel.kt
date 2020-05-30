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

    private val devicesComboBox = JComboBox<MidiDevice.Info>()
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
        devicesComboBox.addActionListener {
            if (devicesComboBox.selectedItem == null) {
                return@addActionListener
            }
            MidiControlProperties.midiDeviceIdentifier =
                deviceInfoToString(devicesComboBox.selectedItem as MidiDevice.Info)
        }

        add(connectButton, BorderLayout.LINE_END)
        connectButton.addActionListener { toggleMidiConnection() }
    }

    private fun refreshDevicesComboBox() {
        devicesComboBox.model = DefaultComboBoxModel(plugin.allMidiDevices.toTypedArray())
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
                Notifications.add("First select a Midi device to connect with", "Midi Control Plugin")
                return
            }

            MidiControlProperties.midiDeviceIdentifier =
                deviceInfoToString(selectedDevice as MidiDevice.Info)

            connectButton.text = "Connecting..."
            plugin.connectMidiDevice()
        }
    }

    private fun setDevicesComboBoxToCurrentDevice() {
        if (plugin.midiDevice() == null && MidiControlProperties.midiDeviceIdentifier.isEmpty()) {
            return
        }

        var selectedItem: MidiDevice.Info? = null
        if (plugin.midiDevice() == null) {
            selectedItem =
                plugin.allMidiDevices.find { deviceInfoToString(it) == MidiControlProperties.midiDeviceIdentifier }
        }
        if (plugin.midiDevice() != null && selectedItem == null) {
            selectedItem = plugin.allMidiDevices.find {
                deviceInfoToString(it) == deviceInfoToString(
                    plugin.midiDevice()!!.deviceInfo
                )
            }
        }

        if (selectedItem == null) {
            return
        }

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