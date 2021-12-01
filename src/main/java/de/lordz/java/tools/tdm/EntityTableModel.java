package de.lordz.java.tools.tdm;

import java.util.HashMap;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Table model for entity data.
 * 
 * @author lordz
 *
 */
public class EntityTableModel<T> extends AbstractTableModel {

	private static final long serialVersionUID = -7923687804480195313L;
	private List<T> entities;
	private HashMap<Integer, EntityDataModelHelper<T>> columnMap;
	
	public EntityTableModel(List<T> entities, HashMap<Integer, EntityDataModelHelper<T>> columnhMap) {
		this.entities = entities;
		this.columnMap = columnhMap;
	}
	
	@Override
	public String getColumnName(int column) {
		var helper = getHelper(column);
		if (helper != null) {
			return helper.getColumnName();
		}

		return super.getColumnName(column);
	}
	
	@Override
	public int getRowCount() {
		return this.entities.size();
	}

	@Override
	public int getColumnCount() {
		return this.columnMap.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (getRowCount() > rowIndex) {
			var entity = this.entities.get(rowIndex);			
			var helper = getHelper(columnIndex);
			if (helper != null) {
				return helper.getValue(entity);
			}
		}

		return null;
	}
	
	public T getEntity(int rowIndex) {
		if (getRowCount() > rowIndex) {
			return this.entities.get(rowIndex);
		}
		
		return null;
	}
	
	private EntityDataModelHelper<T> getHelper(int columnIndex) {
		var mapValue = columnMap.get(columnIndex);
		if (mapValue != null && mapValue instanceof EntityDataModelHelper) {
			return (EntityDataModelHelper<T>)mapValue;
		}
		
		return null;
	}

}
