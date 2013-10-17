package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.DatabaseObjectModel;
import org.symqle.modeler.sql.SchemaSqlModel;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author lvovich
 */
public class MetadataReader implements ModelReader {
    private String driverClassName;
    private String databaseUrl;
    private String user;
    private String password;

    public void setDriverClassName(final String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setDatabaseUrl(final String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public SchemaSqlModel readModel() throws SQLException, ReflectiveOperationException {
        Driver driver = (Driver) Class.forName(driverClassName).newInstance();
        final Properties info = new Properties();
        info.setProperty("user", user);
        info.setProperty("password", password);
        final Connection connection = driver.connect(databaseUrl, info);
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            return readMetadata(metaData);
        } finally {
            connection.close();
        }
    }

    private SchemaSqlModel readMetadata(DatabaseMetaData metaData) throws SQLException {
        final MetadataModel metadataModel = new MetadataModel();
        final Set<String> tableNames = new HashSet<>();
        {
            final ResultSet rs = metaData.getTables(null, null, null, null);
            try {
                while (rs.next()) {
                    final DatabaseObjectModel model = readResultSetRow(rs);
                    metadataModel.addTable(model);
                    tableNames.add(model.getProperties().get("TABLE_NAME"));
                }
            } finally {
                rs.close();
            }
        }
        {
            final ResultSet rs = metaData.getColumns(null, null, null, null);
            try {
                while (rs.next()) {
                    final DatabaseObjectModel model = readResultSetRow(rs);
                    metadataModel.addColumn(model);
                }
            } finally {
                rs.close();
            }
        }

        for (String tableName: tableNames) {
            final ResultSet rs = metaData.getImportedKeys(null, null, tableName);
            final List<DatabaseObjectModel> accumulator = new ArrayList<>();
            try {
                while (rs.next()) {
                    final DatabaseObjectModel fkColumn = new PropertyHolder(readResultSetRow(rs));
                    if ("1".equals(fkColumn.getProperties().get("KEY_SEQ")) && !accumulator.isEmpty()) {
                        metadataModel.addForeignKey(accumulator);
                        accumulator.clear();
                    }
                    accumulator.add(fkColumn);
                }
                if (!accumulator.isEmpty()) {
                    metadataModel.addForeignKey(accumulator);
                }
            } finally {
                rs.close();
            }
        }
        return metadataModel;
    }

    private DatabaseObjectModel readResultSetRow(final ResultSet rs) throws SQLException {
        final Map<String, String> properties = new HashMap<>();
        final ResultSetMetaData rsMetaData = rs.getMetaData();
        final int columnCount = rsMetaData.getColumnCount();
        for (int i= 1; i <= columnCount; i++) {
            properties.put(rsMetaData.getColumnName(i), rs.getString(i));
        }
        return new PropertyHolder(properties);
    }

}
