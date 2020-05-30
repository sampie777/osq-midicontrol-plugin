package nl.sajansen.midicontrol.gui

import gui.utils.createGraphics
import nl.sajansen.midicontrol.MidiControlProperties
import nl.sajansen.midicontrol.MidiControlRefreshable
import nl.sajansen.midicontrol.MidiControlRefreshableRegister
import themes.Theme
import java.awt.*
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.Timer

class MidiStatusPanel : JPanel(), MidiControlRefreshable {

    private val diameter = 10.0
    private val commandLiveTime: Long = 100
    private val aliveAndValidColor = Color.GREEN
    private val aliveAndInvalidColor = Color.ORANGE
    private val deadColor = Theme.get.BACKGROUND_COLOR
    private var commandReceived: Long = System.currentTimeMillis()
    private var commandIsValid: Boolean = false

    init {
        preferredSize = Dimension(diameter.toInt() + 4, diameter.toInt() + 4)
        MidiControlRefreshableRegister.register(this)
    }

    override fun onMidiCommandReceived(command: String) {
        commandReceived = System.currentTimeMillis()

        commandIsValid = command == MidiControlProperties.previousQueItemCommand
                || command == MidiControlProperties.nextQueItemCommand

        repaint()
        resetRepaintTimer()
    }

    private fun resetRepaintTimer() {
        val timer = Timer(0) {
            repaint()
        }
        timer.initialDelay = commandLiveTime.toInt()
        timer.isRepeats = false
        timer.start()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2 = g as Graphics2D

        g2.drawImage(paintCircle(), 0, 0, null)
    }

    private fun paintCircle(): BufferedImage {
        val (bufferedImage, g2: Graphics2D) = createGraphics((diameter + 4).toInt(), (diameter + 4).toInt())
        g2.stroke = BasicStroke(1F)

        val circle = Ellipse2D.Double(
            2.0, 2.0,
            diameter,
            diameter
        )

        if (commandReceived + commandLiveTime < System.currentTimeMillis()) {
            g2.color = deadColor
        } else if (commandIsValid) {
            g2.color = aliveAndValidColor
        } else {
            g2.color = aliveAndInvalidColor
        }

        g2.fill(circle)
        g2.color = Color.BLACK
        g2.draw(circle)

        g2.dispose()
        return bufferedImage
    }
}