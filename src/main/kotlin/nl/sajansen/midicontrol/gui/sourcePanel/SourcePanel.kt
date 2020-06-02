package nl.sajansen.midicontrol.gui.sourcePanel


import GUI
import handles.QueItemTransferHandler
import nl.sajansen.midicontrol.MidiControlPlugin
import nl.sajansen.midicontrol.midi.ByteMidiMessage
import nl.sajansen.midicontrol.queItems.MidiControlQueItem
import objects.notifications.Notifications
import objects.que.Que
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.logging.Logger
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

class SourcePanel(private val plugin: MidiControlPlugin) : JPanel() {
    private val logger = Logger.getLogger(SourcePanel::class.java.name)

    init {
        initGui()
    }

    private fun initGui() {
        layout = BorderLayout(10, 10)
        border = EmptyBorder(10, 10, 0, 10)

        val titleLabel = JLabel("Items")
        add(titleLabel, BorderLayout.PAGE_START)

        val itemListPanel = JPanel(GridLayout(0, 1))
        itemListPanel.add(midiQueItemPanel())

        val scrollPanelInnerPanel = JPanel(BorderLayout())
        scrollPanelInnerPanel.add(itemListPanel, BorderLayout.PAGE_START)
        val scrollPanel = JScrollPane(scrollPanelInnerPanel)
        scrollPanel.border = null
        add(scrollPanel, BorderLayout.CENTER)
    }

    private fun midiQueItemPanel(): JComponent {
        val panel = JPanel(BorderLayout(5, 5))
        panel.border = CompoundBorder(
            CompoundBorder(
                EmptyBorder(5, 0, 5, 0),
                BorderFactory.createMatteBorder(1, 1, 0, 1, Color(180, 180, 180))
            ),
            EmptyBorder(8, 10, 10, 10)
        )

        val nameField = JTextField()
        nameField.toolTipText = "Queue item name"
        val textField = JTextField()
        textField.toolTipText = "MIDI commands seperated by ',' and each message seperated by ';'"

        val addButton = JButton("+")
        addButton.toolTipText = "Click or drag to add"
        addButton.addActionListener {
            if (textField.text.isEmpty() || nameField.text.isEmpty()) {
                return@addActionListener
            }

            val queItem = inputTextToQueItem(nameField.text, textField.text) ?: return@addActionListener
            Que.add(queItem)

            nameField.text = ""
            textField.text = ""

            GUI.refreshQueItems()
        }
        addButton.transferHandler = QueItemTransferHandler()
        addButton.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (textField.text.isEmpty() || nameField.text.isEmpty()) {
                    return
                }

                val queItem = inputTextToQueItem(nameField.text, textField.text) ?: return

                val transferHandler = (e.source as JButton).transferHandler as QueItemTransferHandler
                transferHandler.queItem = queItem
                transferHandler.exportAsDrag(e.source as JComponent, e, TransferHandler.COPY)

                nameField.text = ""
                textField.text = ""
            }
        })

        val textFieldPanel = JPanel(GridLayout(0, 1))
        textFieldPanel.add(nameField)
        textFieldPanel.add(textField)

        panel.add(JLabel("Midi commands"), BorderLayout.PAGE_START)
        panel.add(textFieldPanel, BorderLayout.CENTER)
        panel.add(addButton, BorderLayout.LINE_END)
        return panel
    }

    private fun inputTextToQueItem(name: String, text: String): MidiControlQueItem? {
        val messages = try {
            hexStringToMidiMessages(text)
        } catch (e: Exception) {
            logger.warning("Failed to convert text '$text' to midi message")
            e.printStackTrace()
            Notifications.add("Failed to create MIDI queue item: ${e.localizedMessage}")
            return null
        }

        return MidiControlQueItem(plugin, name, messages)
    }

    private fun hexStringToMidiMessages(text: String): List<ByteMidiMessage> {
        return text.split(";")
            .map { stringCommand ->
                val byteArray = stringCommand.split(",")
                    .map { stringHexValue ->
                        stringHexValue.trim()
                            .toInt(16)
                            .toByte()
                    }
                    .toByteArray()
                ByteMidiMessage(byteArray)
            }
    }
}