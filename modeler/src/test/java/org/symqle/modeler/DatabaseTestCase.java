package org.symqle.modeler;

import junit.framework.TestCase;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author lvovich
 */
public class DatabaseTestCase extends TestCase {

    private final static DataSource dataSource = createDataSource();

    protected DataSource getDataSource() throws Exception {
        return dataSource;
    }

    private static DataSource createDataSource() {
        try {
            final String config = System.getProperty("org.symqle.integration.config");
            if (config == null) {
                // init embedded database
                final String url = "jdbc:derby:memory:symqle";
                final DataSource initDataSource = new SingleConnectionDataSource(url+";create=true", false);
                initDatabase(initDataSource, "defaultDbSetup.sql");
                return new SingleConnectionDataSource(url, true);
            } else {
                Properties properties = new Properties();
                final File propertiesFile = new File(config);
                properties.load(new FileInputStream(propertiesFile));
                final DataSource initDataSource = new SingleConnectionDataSource(
                        properties.getProperty("symqle.jdbc.driverClass"),
                        properties.getProperty("symqle.jdbc.url"),
                        properties.getProperty("symqle.jdbc.user"),
                        properties.getProperty("symqle.jdbc.password"),
                        false);
                final String databaseName = getDatabaseName(initDataSource);
                String resource = databaseName + "DbSetup.sql";
                initDatabase(initDataSource, resource);
                return initDataSource;
                }
        } catch (Exception e) {
            throw new RuntimeException("Internal error", e);
        }
    }

    private static String getDatabaseName(final DataSource dataSource) throws SQLException {
        final Connection connection = dataSource.getConnection();
        try {
            final DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseProductName();
        } finally {
            connection.close();
        }
    }

    private static void initDatabase(final DataSource dataSource, String resource) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(DatabaseTestCase.class.getClassLoader().getResourceAsStream(resource)));
            try {
                final StringBuilder builder = new StringBuilder();
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        if (line.trim().equals("")) {
                            final String sql = builder.toString();
                            builder.setLength(0);
                            if (sql.trim().length()>0) {
                                System.out.println(sql);
                                final PreparedStatement preparedStatement = connection.prepareStatement(sql);
                                try {
                                    preparedStatement.executeUpdate();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    throw e;
                                }
                                preparedStatement.close();
                            }
                        } else {
                            builder.append(" ").append(line);
                        }
                    }
                final String sql = builder.toString();
                if (sql.trim().length()>0) {
                    System.out.println(sql);
                    final PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    try {
                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw e;
                    }
                    preparedStatement.close();
                }
            } finally {
                reader.close();
            }
        }
    }
}
