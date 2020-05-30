package nl.sajansen.midicontrol

import java.util.logging.Logger

object MidiControlRefreshableRegister {
    private val logger = Logger.getLogger(MidiControlRefreshableRegister::class.java.name)

    private val components: HashSet<MidiControlRefreshable> = HashSet()

    fun onMidiControlEnabled() {
        val componentsCopy = components.toTypedArray()
        for (component in componentsCopy) {
            component.onMidiControlEnabled()
        }
    }

    fun onMidiControlDisabled() {
        val componentsCopy = components.toTypedArray()
        for (component in componentsCopy) {
            component.onMidiControlDisabled()
        }
    }

    fun onMidiDeviceConnected() {
        val componentsCopy = components.toTypedArray()
        for (component in componentsCopy) {
            component.onMidiDeviceConnected()
        }
    }

    fun onMidiDeviceDisconnected() {
        val componentsCopy = components.toTypedArray()
        for (component in componentsCopy) {
            component.onMidiDeviceDisconnected()
        }
    }

    fun onMidiCommandReceived(command: String) {
        val componentsCopy = components.toTypedArray()
        for (component in componentsCopy) {
            component.onMidiCommandReceived(command)
        }
    }

    fun onMidiDevicesUpdated() {
        val componentsCopy = components.toTypedArray()
        for (component in componentsCopy) {
            component.onMidiDevicesUpdated()
        }
    }

    fun onCalibrated(key: String, value: String) {
        val componentsCopy = components.toTypedArray()
        for (component in componentsCopy) {
            component.onCalibrated(key, value)
        }
    }


    fun register(component: MidiControlRefreshable) {
        logger.info("Registering component: ${component::class.java}")
        components.add(component)
    }

    fun isRegistered(component: MidiControlRefreshable): Boolean {
        return components.contains(component)
    }

    fun unregister(component: MidiControlRefreshable) {
        logger.info("Unregistering component: ${component::class.java}")
        components.remove(component)
    }

    fun unregisterAll() {
        logger.info("Unregistering all (${components.size}) components")
        components.clear()
    }

    fun registeredComponents() = components
}