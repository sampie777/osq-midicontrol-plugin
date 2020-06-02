package nl.sajansen.midicontrol

import getCurrentJarDirectory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.logging.Logger

object MidiControlProperties {
    private val logger = Logger.getLogger(MidiControlProperties.toString())

    // En-/disables the creation of a properties file and writing to a properties file.
    // Leave disabled when running tests.
    var writeToFile: Boolean = false

    private val propertiesFilePath =
        getCurrentJarDirectory(this).absolutePath + File.separatorChar + "osq-midicontrol.properties"
    private val properties = Properties()

    var midiDeviceIdentifier: String = ""
    var midiDeviceDescription: String = ""

    val charset = Charsets.ISO_8859_1
    var previousQueItemCommand: String = ""
    var nextQueItemCommand: String = ""

    fun load() {
        logger.info("Loading midicontrol properties")

        if (File(propertiesFilePath).exists()) {
            FileInputStream(propertiesFilePath).use { properties.load(it) }
        } else {
            logger.info("No midicontrol properties file found, using defaults")
        }

        midiDeviceIdentifier = properties.getProperty("midiDeviceIdentifier", midiDeviceIdentifier)
        midiDeviceDescription = properties.getProperty("midiDeviceDescription", midiDeviceDescription)
        previousQueItemCommand = configStringToByteArrayString(
            properties.getProperty(
                "previousQueItemCommand",
                byteArrayStringToConfigString(previousQueItemCommand)
            )
        )
        nextQueItemCommand = configStringToByteArrayString(
            properties.getProperty(
                "nextQueItemCommand",
                byteArrayStringToConfigString(nextQueItemCommand)
            )
        )

        if (!File(propertiesFilePath).exists()) {
            save()
        }
    }

    fun save() {
        logger.info("Saving midicontrol properties")

        properties.setProperty("midiDeviceIdentifier", midiDeviceIdentifier)
        properties.setProperty("midiDeviceDescription", midiDeviceDescription)
        properties.setProperty("previousQueItemCommand", byteArrayStringToConfigString(previousQueItemCommand))
        properties.setProperty("nextQueItemCommand", byteArrayStringToConfigString(nextQueItemCommand))

        if (!writeToFile) {
            return
        }

        logger.info("Creating midicontrol properties file")

        FileOutputStream(propertiesFilePath).use { fileOutputStream ->
            properties.store(
                fileOutputStream,
                "User properties for midicontrol plugin"
            )
        }
    }
}