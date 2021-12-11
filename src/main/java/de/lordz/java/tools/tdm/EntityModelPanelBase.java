package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;

import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.IEntityId;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public abstract class EntityModelPanelBase<T extends IEntityId> extends JPanel {
    
    private static final long serialVersionUID = -2612431145357781452L;
    protected static final DefaultTableCellRenderer RightTableCellRenderer = CreateTableCellRightRenderer();
    
    private final JButton buttonNew;
    private final JButton buttonEdit;
    private final JButton buttonDelete;
    private final JPanel panelDataContent;
    private final Class<? extends T> entityType;
    private final JTable table;
    private Component contentComponent;
    private HashMap<Integer, T> cachedEntities;
    
    public EntityModelPanelBase(Class<? extends T> entityType) {
        this.entityType = entityType;
        setLayout(new BorderLayout(0, 0));
        var contentPanel = new JPanel(new BorderLayout(0,0));
        add(contentPanel, BorderLayout.CENTER);
        
        var scrollPaneTrips = new JScrollPane();
        contentPanel.add(scrollPaneTrips, BorderLayout.WEST);
        this.table = new JTable();
        this.table.setShowGrid(true);
        this.table.setColumnSelectionAllowed(false);
        this.table.setDragEnabled(false);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.table.getTableHeader().setReorderingAllowed(false);
        this.table.getSelectionModel().addListSelectionListener(e -> internalPerformTableSelectionChanged(e));
        scrollPaneTrips.setViewportView(this.table);
        scrollPaneTrips.setPreferredSize(new Dimension(400, 100));
        
        var panelRightContent = new JPanel();
        contentPanel.add(panelRightContent, BorderLayout.CENTER);
        panelRightContent.setLayout(new BorderLayout(0, 0));
        
        var toolbar = new JToolBar();
        toolbar.setFloatable(false);
        panelRightContent.add(toolbar, BorderLayout.NORTH);
        
        this.buttonNew = new JButton(IconFontSwing.buildIcon(FontAwesome.PLUS_CIRCLE, 20, new Color(0, 150, 0)));
        this.buttonNew.setToolTipText(LocalizationProvider.getString("mainframe.button.new"));
        this.buttonNew.addActionListener(e -> performActionNew());
        toolbar.add(this.buttonNew);
        
        this.buttonEdit = new JButton(IconFontSwing.buildIcon(FontAwesome.PENCIL_SQUARE, 20, Color.ORANGE));
        this.buttonEdit.setToolTipText(LocalizationProvider.getString("mainframe.button.edit"));
        this.buttonEdit.addActionListener(e -> performEditDeleteItem(true));
        toolbar.add(this.buttonEdit);
        
        this.buttonDelete = new JButton(IconFontSwing.buildIcon(FontAwesome.MINUS_CIRCLE, 20, new Color(150, 0, 0)));
        this.buttonDelete.setToolTipText(LocalizationProvider.getString("mainframe.button.delete"));
        this.buttonDelete.addActionListener(e -> performEditDeleteItem(false));
        toolbar.add(this.buttonDelete);
        
        this.panelDataContent = new JPanel();
        panelRightContent.add(panelDataContent, BorderLayout.CENTER);
        this.panelDataContent.setLayout(new BorderLayout(0, 0));
        
    }
    
    /**
     * Reloads the table data.
     */
    public void reloadTable() {
        performReloadTableViewModel();
    }
    
    /**
     * Sets whether the table draws grid lines around cells
     * 
     * @param showGrid If <CODE>true</CODE> shows the grid lines otherwise not.
     */
    public void setShowGrid(boolean showGrid) {
        this.table.setShowGrid(showGrid);
    }
    
    /**
     * Sets the cached entities.
     * 
     * @param entities The list of entities to cache.
     */
    public void setCachedEntities(List<T> entities) {
        this.cachedEntities = createEntityIdHashMap(entities);
    }
    
    /**
     * Retrieves the cached entities hash map.
     * 
     * @return The cached <CODE>HashMap<Integer, T></CODE> containing the entities.
     */
    public HashMap<Integer, T> getCachedEntities() {
        return this.cachedEntities;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.buttonNew.setEnabled(enabled);
        this.buttonEdit.setEnabled(enabled);
        this.buttonDelete.setEnabled(enabled);
        if (contentComponent != null) {
            contentComponent.setEnabled(enabled);
        }
    }
    
    abstract void performTableSelectionChanged(ListSelectionEvent event);
    abstract void performActionNew();
    abstract void performEditDeleteItem(boolean edit);
    abstract void performReloadTableViewModel();
    
    protected void setContentComponent(Component component) {
        this.contentComponent = component;
        this.panelDataContent.add(component, BorderLayout.NORTH);
    }
    
    protected JTable getTable() {
        return this.table;
    }
    
    protected T getSelectedEntity() {
        return getSelectedEntity(this.table, this.entityType);
    }
    
    private void internalPerformTableSelectionChanged(ListSelectionEvent event) {
        if (event != null && event.getValueIsAdjusting()) {
            return;
        }
        
        performTableSelectionChanged(event);
    }
    
    private static <T> T getSelectedEntity(JTable table, Class<? extends T> type) {
        T entity = null;
        if (table != null) {
            var selectedRowIndex = table.getSelectedRow();
            if (selectedRowIndex >= 0) {
                var currentModel = table.getModel();
                if (currentModel != null && currentModel instanceof EntityTableModel) {
                    var entityTableModel = EntityTableModel.class.cast(currentModel);
                    if (entityTableModel != null) {
                        var value = entityTableModel.getEntity(selectedRowIndex);
                        if (value != null) {
                            entity = type.cast(value);
                        }
                    }
                }
            }
        }

        return entity;
    }
    
    private HashMap<Integer, T> createEntityIdHashMap(List<T> entities) {
        if (entities != null) {
            var hashMap = new HashMap<Integer, T>(entities.size());
            for (var entity : entities) {
                if (entity != null) {
                    hashMap.put(entity.getId(), entity);
                }
            }
            
            return hashMap;
        }
        
        return new HashMap<Integer, T>(0);
    }
    
    private static DefaultTableCellRenderer CreateTableCellRightRenderer() {
        var rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        return rightRenderer;
    }
}