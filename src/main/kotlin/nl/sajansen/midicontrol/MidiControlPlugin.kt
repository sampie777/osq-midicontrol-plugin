package nl.sajansen.midicontrol

import nl.sajansen.midicontrol.gui.detailPanel.DetailPanel
import nl.sajansen.midicontrol.gui.sourcePanel.SourcePanel
import nl.sajansen.midicontrol.midi.MidiDeviceClass
import nl.sajansen.midicontrol.midi.MidiReceiver
import nl.sajansen.midicontrol.queItems.MidiControlQueItem
import objects.notifications.Notifications
import objects.que.JsonQue
import objects.que.QueItem
import plugins.common.DetailPanelBasePlugin
import plugins.common.QueItemBasePlugin
import java.awt.Color
import java.net.URL
import java.util.logging.Logger
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JComponent

class MidiControlPlugin : DetailPanelBasePlugin, QueItemBasePlugin {
    private val logger = Logger.getLogger(MidiControlPlugin::class.java.name)

    override val name = "MidiControlPlugin"
    override val description = "Control your queue using a Midi device"
    override val version = PluginInfo.version
    override val icon: Icon? = createImageIcon("/nl/sajansen/midicontrol/icon-14.png")

    override val tabName = "Midi"

    internal val quickAccessColor = Color(255, 239, 230)

    var isMidiControlOn: Boolean = true
    var allMidiDevices = ArrayList<MidiDeviceClass>()
    private var activeMidiDeviceClass: MidiDeviceClass? = null
    fun activeMidiDevice() = activeMidiDeviceClass

    var calibratingPreviousCommand: Boolean = false
    var calibratingNextCommand: Boolean = false

    override fun enable() {
        super<DetailPanelBasePlugin>.enable()
        super<QueItemBasePlugin>.enable()

        MidiControlProperties.writeToFile = true
        MidiControlProperties.load()
        getAllMidiDevicesClasses()
    }

    override fun disable() {
        super<DetailPanelBasePlugin>.disable()
        super<QueItemBasePlugin>.disable()

        disconnectMidiDevice()
        MidiControlProperties.save()
        MidiControlProperties.writeToFile = false
    }

    override fun sourcePanel(): JComponent {
        return SourcePanel(this)
    }

    override fun configStringToQueItem(value: String): QueItem {
        throw NotImplementedError("This method is deprecated")
    }

    override fun jsonToQueItem(jsonQueItem: JsonQue.QueItem): QueItem {
        return MidiControlQueItem.fromJson(this, jsonQueItem)
    }

    override fun detailPanel(): JComponent {
        return DetailPanel(this)
    }

    private fun getAllMidiDevicesClasses(): ArrayList<MidiDeviceClass> {
        val midiDevices = MidiSystem.getMidiDeviceInfo()
        allMidiDevices = midiDevices.toList().map { MidiDeviceClass(it) } as ArrayList<MidiDeviceClass>
        MidiControlRefreshableRegister.onMidiDevicesUpdated()
        return allMidiDevices
    }

    /**
     * Connect to a specified midi device. Returns wether or not the connection was successful
     */
    private fun connectToMidiDevice(midiDevice: MidiDevice): Boolean {
        logger.info("Setting up connection with MidiDevice: ${midiDevice.deviceInfo.name}")

        // Register receiver for transmitters
        try {
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
        activeMidiDeviceClass?.device?.close()
        MidiControlRefreshableRegister.onMidiDeviceDisconnected()
    }

    fun connectMidiDevice(deviceClass: MidiDeviceClass) {
        MidiControlProperties.midiDeviceIdentifier = deviceClass.id
        activeMidiDeviceClass = deviceClass

        if (deviceClass.device == null) {
            logger.info("Midi device '${deviceClass.id}' not found")
            Notifications.add("Midi device '${deviceClass.id}' not found", "Midi Control")
            return
        }

        if (connectToMidiDevice(deviceClass.device!!)) {
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
        return activeMidiDeviceClass != null && activeMidiDeviceClass?.device != null && activeMidiDeviceClass!!.device!!.isOpen
    }
}