package nl.sajansen.midicontrol

import objects.que.Que
import java.util.logging.Logger
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

class MidiReceiver(private val plugin: MidiControlPlugin, private val device: MidiDevice) : Receiver, MidiControlRefreshable {

    private val logger = Logger.getLogger(MidiReceiver::class.java.name)

    init {
        MidiControlRefreshableRegister.register(this)
    }

    override fun send(message: MidiMessage, timestamp: Long) {
        val command = String(message.message, MidiControlProperties.charset)
        MidiControlRefreshableRegister.onMidiCommandReceived(command)

        doCalibration(command)

        if (!plugin.isMidiControlOn) {
            logger.info("Omitting midi control because it's off")
            return
        }

        when (command) {
            MidiControlProperties.previousQueItemCommand -> {
                logger.info("Received Midi command for previous queue item")
                Que.previous()
            }
            MidiControlProperties.nextQueItemCommand -> {
                logger.info("Received Midi command for next queue item")
                Que.next()
            }
            else -> logger.info("Unknown command received: $command")
        }
    }

    private fun doCalibration(command: String) {
        if (plugin.calibratingPreviousCommand) {
            logger.info("Saving new value for previous Que Item command")
            MidiControlProperties.previousQueItemCommand = command
            plugin.calibratingPreviousCommand = false
            MidiControlRefreshableRegister.onCalibrated("Previous", command)
            MidiControlProperties.save()
        }
        if (plugin.calibratingNextCommand) {
            logger.info("Saving new value for next Que Item command")
            MidiControlProperties.nextQueItemCommand = command
            plugin.calibratingNextCommand = false
            MidiControlRefreshableRegister.onCalibrated("Next", command)
            MidiControlProperties.save()
        }
    }

    override fun close() {
        logger.info("Closing Midi connection")
        try {
            device.close()
        } catch (e: Exception) {
            logger.info("Exception occurred during closing of midi device")
            e.printStackTrace()
        }
    }

    override fun onMidiDeviceDisconnected() {
        close()
    }
}