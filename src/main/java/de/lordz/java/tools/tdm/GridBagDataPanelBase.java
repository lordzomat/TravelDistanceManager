package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

/**
 * Base class for a grid bag data panel.
 * 
 * @author lordzomat
 *
 */
public class GridBagDataPanelBase extends JPanel {
    
    private static final long serialVersionUID = -8076573223319372932L;
    private final JPanel contentPanel;

    /**
     * Initializes a new instance of the <CODE>EntityDataPanelBase</CODE> class.
     * 
     * @param columnWidths The overrides to the column minimum width.
     * @param rowHeights The overrides to the column minimum height.
     * @param columnWeights The overrides to the column weights
     * @param rowWeights The overrides to the row weights
     */
    protected GridBagDataPanelBase(int[] columnWidths, int[] rowHeights, double[] columnWeights, double[] rowWeights) {
        var gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = columnWidths;
        gridBagLayout.rowHeights = rowHeights;
        gridBagLayout.columnWeights = columnWeights;
        gridBagLayout.rowWeights = rowWeights;
        this.contentPanel = new JPanel();
        this.contentPanel.setLayout(gridBagLayout);
        setLayout(new BorderLayout(0, 0));
        add(this.contentPanel, BorderLayout.WEST);
    }
    
    /**
     * Adds a label to the grid bag.
     * 
     * @param column The column in which the label must be placed.
     * @param row The row in which the label must be placed.
     * @param text The text of the label.
     */
    protected void AddLabel(int column, int row, String text) {
        var labelCustomer = new JLabel(text + ":");
        var constraint = new GridBagConstraints();
        constraint.anchor = GridBagConstraints.NORTHWEST;
        constraint.fill = GridBagConstraints.NONE;
        constraint.insets = new Insets(0, 5, 5, 5);
        constraint.gridx = column;
        constraint.gridy = row;
        this.contentPanel.add(labelCustomer, constraint);
    }
    
    /**
     * Adds a input component to the grid bag.
     * 
     * @param column The column in which the component must be placed.
     * @param row The row in which the component must be placed.
     * @param component The component to add.
     */
    protected void AddInput(int column, int row, Component component) { 
        AddInput(column, row, component, GridBagConstraints.VERTICAL);
    }
    
    /**
     * Adds a input component to the grid bag.
     * 
     * @param column The column in which the component must be placed.
     * @param row The row in which the component must be placed.
     * @param component The component to add.
     * @param fill The fill mode.
     */
    protected void AddInput(int column, int row, Component component, int fill) {
        var constraint = new GridBagConstraints();
        constraint.anchor = GridBagConstraints.WEST;
        constraint.fill = fill;
        constraint.insets = new Insets(0, 0, 5, 0);
        constraint.gridx = column;
        constraint.gridy = row;
        this.contentPanel.add(component, constraint);
    }
    
    /**
     * Sets the text of the specified <CODE>JTextComponent</CODE> and moves the caret position to position 0.
     * 
     * @param textComponent The field on which the text must be set.
     * @param text The text to set.
     */
    protected void setText(JTextComponent textComponent, String text) {
        textComponent.setText(text);
        textComponent.setCaretPosition(0);
    }
    
    /**
     * Retrieves the content panel.
     * @return The <CODE>JPanel</CODE> containing the content components.
     */
    protected JPanel getGridBagContentPanel() {
        return this.contentPanel;
    }
}
