package nl.sajansen.midicontrol.gui

import nl.sajansen.midicontrol.MidiControlPlugin
import nl.sajansen.midicontrol.MidiControlRefreshable
import nl.sajansen.midicontrol.MidiControlRefreshableRegister
import java.awt.Component
import java.awt.Dimension
import java.util.logging.Logger
import javax.swing.*

class MidiCalibrationPanel(private val plugin: MidiControlPlugin) : JPanel(), MidiControlRefreshable {
    private val logger = Logger.getLogger(MidiCalibrationPanel::class.java.name)

    private val calibratePreviousCommandButton = JButton()
    private val calibrateNextCommandButton = JButton()

    init {
        initGui()

        refreshCalibrationButtons()
        if (!plugin.isConnected()) {
            onMidiDeviceDisconnected()
        }

        MidiControlRefreshableRegister.register(this)
    }

    private fun initGui() {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        alignmentX = Component.LEFT_ALIGNMENT

        add(JLabel("Calibration"))
        add(Box.createRigidArea(Dimension(0, 5)))

        add(calibratePreviousCommandButton)
        add(Box.createRigidArea(Dimension(0, 5)))
        calibratePreviousCommandButton.addActionListener {
            logger.info("Toggling calibration for Previous Que Item command")
            plugin.calibratingPreviousCommand = !plugin.calibratingPreviousCommand
            refreshCalibrationButtons()
        }

        add(calibrateNextCommandButton)
        add(Box.createRigidArea(Dimension(0, 5)))
        calibrateNextCommandButton.addActionListener {
            logger.info("Toggling calibration for Next Que Item command")
            plugin.calibratingNextCommand = !plugin.calibratingNextCommand
            refreshCalibrationButtons()
        }
    }

    private fun refreshCalibrationButtons() {
        calibratePreviousCommandButton.text = if (plugin.calibratingPreviousCommand) "Calibrating..." else "Previous queue item"
        calibrateNextCommandButton.text = if (plugin.calibratingNextCommand) "Calibrating..." else "Next queue item"
    }

    override fun onCalibrated(key: String, value: String) {
        refreshCalibrationButtons()
    }

    override fun onMidiDeviceConnected() {
        calibratePreviousCommandButton.isEnabled = true
        calibrateNextCommandButton.isEnabled = true
    }

    override fun onMidiDeviceDisconnected() {
        calibratePreviousCommandButton.isEnabled = false
        calibrateNextCommandButton.isEnabled = false
    }
}