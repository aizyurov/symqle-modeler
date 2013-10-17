package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.ColumnPair;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.DatabaseObjectModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public class MetadataModel implements SchemaSqlModel {

    final Map<String, TableModel> tables = new LinkedHashMap<>();
    /**
     * Key is table name
     * ColumnModels are ordered by seqeuqntial number
     */
    final Map<String, Map<String, ColumnModel>> columnsByTable = new HashMap<>();

    final Map<String, List<ForeignKeyModel>> foreignKeysByTable = new HashMap<>();

    private class TableModel extends PropertyHolder implements TableSqlModel {

        private TableModel(final DatabaseObjectModel model) {
            super(model);
        }

        private String getName() {
            return getProperties().get("TABLE_NAME");
        }

        @Override
        public List<ColumnSqlModel> getColumns() {
            return new ArrayList<ColumnSqlModel>(columnsByTable.get(getName()).values());
        }

        @Override
        public List<ForeignKeySqlModel> getForeignKeys() {
            return new ArrayList<ForeignKeySqlModel>(foreignKeysByTable.get(getName()));
        }

    }

    private class ForeignKeyModel implements ForeignKeySqlModel {

        private final List<DatabaseObjectModel> fkColumns;
        private final Map<String, String> extraProperties = new HashMap<>();

        private ForeignKeyModel(final List<DatabaseObjectModel> fkColumns) {
            this.fkColumns = new ArrayList<>(fkColumns);
        }

        private ForeignKeyModel(final List<DatabaseObjectModel> fkColumns, final Map<String, String> extraProperties) {
            this.fkColumns = new ArrayList<>(fkColumns);
            this.extraProperties.putAll(extraProperties);
        }


        @Override
        public List<DatabaseObjectModel> getColumns() {
            return new ArrayList<>(fkColumns);
        }

        @Override
        public List<ColumnPair> getMapping() {
            final List<ColumnPair> map = new ArrayList<>();
            for (DatabaseObjectModel fkColumn: fkColumns) {
                final String fKtableName = fkColumn.getProperties().get("FKTABLE_NAME");
                final String fKcolumnName = fkColumn.getProperties().get("FKCOLUMN_NAME");
                final String pKtableName = fkColumn.getProperties().get("PKTABLE_NAME");
                final String pKcolumnName = fkColumn.getProperties().get("PKCOLUMN_NAME");
                map.add(new ColumnPair(columnsByTable.get(fKtableName).get(fKcolumnName),
                        columnsByTable.get(pKtableName).get(pKcolumnName)));
            }
            return map;
        }

        @Override
        public TableSqlModel getTable() {
            return tables.get(fkColumns.get(0).getProperties().get("FKTABLE_NAME"));
        }

        @Override
        public TableSqlModel getReferencedTable() {
            return tables.get(fkColumns.get(0).getProperties().get("PKTABLE_NAME"));
        }

        @Override
        public Map<String, String> getProperties() {
            final Map<String, String> properties = new HashMap<>();
            properties.putAll(fkColumns.get(0).getProperties());
            properties.putAll(extraProperties);
            return properties;
        }
    }



    private class ColumnModel extends PropertyHolder implements ColumnSqlModel {

        private ColumnModel(final DatabaseObjectModel model) {
            super(model);
        }

        @Override
        public TableSqlModel getOwner() {
            return MetadataModel.this.tables.get(properties.get("TABLE_NAME"));
        }

    }


    void addColumn(final DatabaseObjectModel properties) {
        final ColumnModel column = new ColumnModel(properties);
        final Map<String, ColumnModel> tableColumns = columnsByTable.get(column.getProperties().get("TABLE_NAME"));
        if (tableColumns != null) {
            tableColumns.put(column.getProperties().get("COLUMN_NAME"), column);
        }
        // silently ignore columns from unknown tables
    }

    void addTable(final DatabaseObjectModel properties) {
        TableModel tableModel = new TableModel(properties);
        final String tableName = tableModel.getName();
        tables.put(tableName, tableModel);
        columnsByTable.put(tableName, new LinkedHashMap<String, ColumnModel>());
        foreignKeysByTable.put(tableName, new ArrayList<ForeignKeyModel>());
    }


    void addForeignKey(final List<DatabaseObjectModel> accumulator) {
        addForeignKey(accumulator, Collections.<String, String>emptyMap());
    }

    void addForeignKey(final List<DatabaseObjectModel> accumulator, final Map<String, String> extraProperties) {
        final ForeignKeyModel foreignKeyModel = new ForeignKeyModel(accumulator, extraProperties);
        final List<ForeignKeyModel> tableForeignKeys = foreignKeysByTable.get(foreignKeyModel.getProperties().get("FKTABLE_NAME"));
        if (!tables.containsKey(foreignKeyModel.getProperties().get("FKTABLE_NAME"))) {
            // ignore foreign keys from unknown tables
            return;
        }
        if (!tables.containsKey(foreignKeyModel.getProperties().get("PKTABLE_NAME"))) {
            // ignore foreign keys to unknown tables
            return;
        }
        final Map<String, ColumnModel> tableColumns = columnsByTable.get(foreignKeyModel.getProperties().get("FKTABLE_NAME"));
        final Map<String, ColumnModel> pkTableColumns = columnsByTable.get(foreignKeyModel.getProperties().get("PKTABLE_NAME"));
        for (DatabaseObjectModel fkColumn: accumulator) {
            final String columnName = fkColumn.getProperties().get("FKCOLUMN_NAME");
            final String pkColumnName = fkColumn.getProperties().get("PKCOLUMN_NAME");
            if (!tableColumns.containsKey(columnName) || !pkTableColumns.containsKey(pkColumnName)) {
                // ignore foreign keys containing unknown columns
                return;
            }
        }
        tableForeignKeys.add(foreignKeyModel);
    }

    @Override
    public List<TableSqlModel> getTables() {
        return new ArrayList<TableSqlModel>(tables.values());
    }
}
