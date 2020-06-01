package nl.sajansen.midicontrol

import nl.sajansen.midicontrol.gui.DetailPanel
import objects.notifications.Notifications
import plugins.common.DetailPanelBasePlugin
import java.net.URL
import java.util.logging.Logger
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JComponent

class MidiControlPlugin : DetailPanelBasePlugin {
    private val logger = Logger.getLogger(MidiControlPlugin::class.java.name)

    override val name = "MidiControlPlugin"
    override val description = "Control your queue using a Midi device"
    override val version = PluginInfo.version
    override val icon: Icon? = createImageIcon("/nl/sajansen/midicontrol/icon-14.png")

    override val tabName = "Midi"

    var isMidiControlOn: Boolean = true
    var allMidiDevices = ArrayList<MidiDevice.Info>()
    private var midiDevice: MidiDevice? = null
    fun midiDevice() = midiDevice

    var calibratingPreviousCommand: Boolean = false
    var calibratingNextCommand: Boolean = false

    override fun enable() {
        super.enable()
        MidiControlProperties.writeToFile = true
        MidiControlProperties.load()
        getAllMidiDevices()
    }

    override fun disable() {
        super.disable()
        disconnectMidiDevice()
        MidiControlProperties.save()
        MidiControlProperties.writeToFile = false
    }

    override fun detailPanel(): JComponent {
        return DetailPanel(this)
    }

    private fun getAllMidiDevices(): Array<out MidiDevice.Info> {
        val midiDevices = MidiSystem.getMidiDeviceInfo()
        allMidiDevices = midiDevices.toList() as ArrayList<MidiDevice.Info>
        MidiControlRefreshableRegister.onMidiDevicesUpdated()
        return midiDevices
    }

    private fun getMidiDevice(name: String): MidiDevice? {
        val deviceInfo = getAllMidiDevices().find { deviceInfoToString(it) == name } ?: return null
        return MidiSystem.getMidiDevice(deviceInfo)
    }

    /**
     * Connect to a specified midi device. Returns wether or not the connection was successful
     */
    private fun connectToMidiDevice(midiDevice: MidiDevice): Boolean {
        logger.info("Setting up connection with MidiDevice: ${midiDevice.deviceInfo.name}")

        // Register receiver for transmitters
        try {
            midiDevice.transmitters.forEach { transmitter ->
                logger.info("Creating midi receiver for transmitter: $transmitter")
                transmitter.receiver = MidiReceiver(this, midiDevice)
            }

            midiDevice.transmitter.receiver = MidiReceiver(this, midiDevice)
        } catch (e: Exception) {
            logger.warning("Failed to set transmitters")
            e.printStackTrace()
            Notifications.add(
                "Failed to setup connection with device: ${midiDevice.deviceInfo.name}: ${e.localizedMessage}",
                "Midi Control"
            )
            return false
        }

        // Open connection with device
        try {
            midiDevice.open()
        } catch (e: Exception) {
            logger.severe("Failed to open device: ${midiDevice.deviceInfo.name}")
            e.printStackTrace()
            Notifications.add(
                "Failed to connect with device: ${midiDevice.deviceInfo.name}: ${e.localizedMessage}",
                "Midi Control"
            )
            return false
        }

        logger.info("Connected to: ${midiDevice.deviceInfo.name}")
        return true
    }

    private fun createImageIcon(path: String): ImageIcon? {
        val imgURL: URL? = javaClass.getResource(path)
        if (imgURL != null) {
            return ImageIcon(imgURL)
        }

        logger.severe("Couldn't find imageIcon: $path")
        return null
    }

    fun disconnectMidiDevice() {
        midiDevice?.close()
        MidiControlRefreshableRegister.onMidiDeviceDisconnected()
    }

    fun connectMidiDevice() {
        midiDevice = getMidiDevice(MidiControlProperties.midiDeviceIdentifier)

        if (midiDevice == null) {
            logger.info("Midi device '${MidiControlProperties.midiDeviceIdentifier}' not found")
            Notifications.add("Midi device '${MidiControlProperties.midiDeviceIdentifier}' not found", "Midi Control")
            return
        }

        if (connectToMidiDevice(midiDevice!!)) {
            MidiControlRefreshableRegister.onMidiDeviceConnected()
        } else {
            MidiControlRefreshableRegister.onMidiDeviceDisconnected()
        }
    }

    fun enableMidiControl() {
        logger.info("Enabling Midi control")
        isMidiControlOn = true
        MidiControlRefreshableRegister.onMidiControlEnabled()
    }

    fun disableMidiControl() {
        logger.info("Disabling Midi control")
        isMidiControlOn = false
        MidiControlRefreshableRegister.onMidiControlDisabled()
    }

    fun isConnected(): Boolean {
        return midiDevice() != null && midiDevice()!!.isOpen
    }
}