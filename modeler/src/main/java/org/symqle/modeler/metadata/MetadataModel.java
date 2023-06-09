package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.ColumnPair;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.DatabaseObjectModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.PrimaryKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

import java.util.*;

/**
 * @author lvovich
 */
public class MetadataModel implements SchemaSqlModel {

    final Map<String, TableModel> tables = new LinkedHashMap<>();

    final String databaseName;

    public MetadataModel(final String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Key is table name
     * ColumnModels are ordered by seqeuqntial number
     */
    final Map<String, Map<String, ColumnModel>> columnsByTable = new HashMap<>();

    final Map<String, List<ForeignKeyModel>> foreignKeysByTable = new HashMap<>();

    final Map<String, PrimaryKeyModel> primaryKeysByTable =  new HashMap<>();

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

        @Override
        public PrimaryKeySqlModel getPrimaryKey() {
            return primaryKeysByTable.get(getName());
        }

        @Override
        public Set<String> getGeneratedKeys() {
            final Set<String> keys = new TreeSet<>();
            for (ColumnSqlModel column: getColumns()) {
                final String generatedKey = column.getProperties().get("GENERATED_KEY");
                if (generatedKey != null) {
                    keys.add(generatedKey);
                }
            }
            return keys;
        }


        @Override
        public String toString() {
            return getProperties().get("TABLE_TYPE") + " " + getProperties().get("TABLE_NAME");
        }

    }

    private class ForeignKeyModel implements ForeignKeySqlModel {

        private final List<DatabaseObjectModel> fkColumns;
        private final Map<String, String> extraProperties = new HashMap<>();

        private ForeignKeyModel(final List<DatabaseObjectModel> fkColumns, final Map<String, String> extraProperties) {
            this.fkColumns = new ArrayList<>(fkColumns);
            this.extraProperties.putAll(extraProperties);
        }


        @Override
        public List<DatabaseObjectModel> getColumnProperties() {
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

        @Override
        public String toString() {
            return getProperties().get("FK_NAME");
        }

    }



    private class ColumnModel extends PropertyHolder implements ColumnSqlModel {

        private ColumnModel(final DatabaseObjectModel model) {
            super(model);
        }

        @Override
        public TableSqlModel getOwner() {
            return MetadataModel.this.tables.get(getProperties().get("TABLE_NAME"));
        }

        @Override
        public String toString() {
            return getProperties().get("TABLE_NAME") +"." + getProperties().get("COLUMN_NAME");
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

    void addPrimaryKey(final List<? extends DatabaseObjectModel> accumulator) {
        final String tableName = accumulator.get(0).getProperties().get("TABLE_NAME");
        final Map<String, ColumnModel> tableColumns = columnsByTable.get(tableName);
        if (tableColumns == null) {
            // primary key of deleted table
            return;
        }
        for (DatabaseObjectModel objectModel : accumulator) {
            if (!tableColumns.containsKey(objectModel.getProperties().get("COLUMN_NAME"))) {
                // primary key references to unkown column; do not add
                return;
            }
        }
            primaryKeysByTable.put(tableName, new PrimaryKeyModel(accumulator));
    }

    void addForeignKey(final List<DatabaseObjectModel> accumulator, final Map<String, String> extraProperties) {
        final Map<String, String> fkProperties = accumulator.get(0).getProperties();
        final String fkTableName = fkProperties.get("FKTABLE_NAME");
        final List<ForeignKeyModel> tableForeignKeys = foreignKeysByTable.get(fkTableName);
        if (!tables.containsKey(fkTableName)) {
            // ignore foreign keys from unknown tables
            return;
        }
        final String pkTableName = fkProperties.get("PKTABLE_NAME");
        if (!tables.containsKey(pkTableName)) {
            // ignore foreign keys to unknown tables
            return;
        }
        final Map<String, ColumnModel> tableColumns = columnsByTable.get(fkTableName);
        final Map<String, ColumnModel> pkTableColumns = columnsByTable.get(pkTableName);
        for (DatabaseObjectModel fkColumn: accumulator) {
            final String columnName = fkColumn.getProperties().get("FKCOLUMN_NAME");
            final String pkColumnName = fkColumn.getProperties().get("PKCOLUMN_NAME");
            if (!tableColumns.containsKey(columnName) || !pkTableColumns.containsKey(pkColumnName)) {
                // ignore foreign keys containing unknown columns
                return;
            }
        }
        tableForeignKeys.add(new ForeignKeyModel(accumulator, extraProperties));
    }

    @Override
    public List<TableSqlModel> getTables() {
        return new ArrayList<TableSqlModel>(tables.values());
    }

    private class PrimaryKeyModel implements PrimaryKeySqlModel {

        List<DatabaseObjectModel> columns = new ArrayList<>();

        private PrimaryKeyModel(final List<? extends DatabaseObjectModel> accumulator) {
            columns.addAll(accumulator);
        }

        @Override
        public List<ColumnSqlModel> getColumns() {
            final List<ColumnSqlModel> result = new ArrayList<>();
            for (DatabaseObjectModel column : columns) {
                final Map<String, ColumnModel> table = columnsByTable.get(column.getProperties().get("TABLE_NAME"));
                final ColumnModel columnModel = table
                        .get(column.getProperties().get("COLUMN_NAME"));
                result.add(columnModel);
            }
            return result;
        }
    }
}
