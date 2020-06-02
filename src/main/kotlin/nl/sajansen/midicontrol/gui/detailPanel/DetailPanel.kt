package nl.sajansen.midicontrol.gui.detailPanel

import nl.sajansen.midicontrol.MidiControlPlugin
import nl.sajansen.midicontrol.MidiControlRefreshable
import nl.sajansen.midicontrol.MidiControlRefreshableRegister
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.util.logging.Logger
import javax.swing.*
import javax.swing.border.EmptyBorder

class DetailPanel(private val plugin: MidiControlPlugin) : JPanel(),
    MidiControlRefreshable {
    private val logger = Logger.getLogger(DetailPanel::class.java.name)

    private val midiOnOffButton = JButton()

    init {
        initGui()

        refreshOnOffButtonText()

        MidiControlRefreshableRegister.register(this)
    }

    private fun initGui() {
        layout = BorderLayout()
        border = EmptyBorder(10, 10, 10, 10)

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
        panel.alignmentX = Component.LEFT_ALIGNMENT

        midiOnOffButton.addActionListener { toggleMidiOnOff() }

        val actionPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 10))
        actionPanel.alignmentX = Component.LEFT_ALIGNMENT
        actionPanel.add(MidiStatusPanel())
        actionPanel.add(midiOnOffButton)


        panel.add(MidiConnectionPanel(plugin))
        panel.add(Box.createRigidArea(Dimension(0, 15)))
        panel.add(actionPanel)
        panel.add(Box.createRigidArea(Dimension(0, 15)))
        panel.add(MidiCalibrationPanel(plugin))

        val scrollPanelInnerPanel = JPanel(BorderLayout())
        scrollPanelInnerPanel.add(panel, BorderLayout.PAGE_START)
        val scrollPanel = JScrollPane(scrollPanelInnerPanel)
        scrollPanel.border = null
        add(scrollPanel, BorderLayout.CENTER)
    }


    private fun toggleMidiOnOff() {
        if (plugin.isMidiControlOn) {
            plugin.disableMidiControl()
        } else {
            plugin.enableMidiControl()
        }
    }

    private fun refreshOnOffButtonText() {
        midiOnOffButton.text = if (plugin.isMidiControlOn) "Control on" else "Control off"
    }

    override fun onMidiControlEnabled() {
        refreshOnOffButtonText()
    }

    override fun onMidiControlDisabled() {
        refreshOnOffButtonText()
    }
}