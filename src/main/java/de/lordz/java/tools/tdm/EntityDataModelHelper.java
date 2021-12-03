package de.lordz.java.tools.tdm;

import java.util.function.Function;

public class EntityDataModelHelper<T> {
    private String columnName;
    private Function<T, Object> getValueFunction;

    public EntityDataModelHelper(String columnName, Function<T, Object> getValueFunction) {
        this.columnName = columnName;
        this.getValueFunction = getValueFunction;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public Object getValue(T entity) {
        return this.getValueFunction.apply(entity);
    }
}
