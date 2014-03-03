package org.symqle.modeler;

import junit.framework.TestCase;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.symqle.modeler.generator.FreeMarkerClassWriter;
import org.symqle.modeler.metadata.ColumnJavaNameAppender;
import org.symqle.modeler.metadata.ForeignKeyJavaNameAppender;
import org.symqle.modeler.metadata.MetadataReader;
import org.symqle.modeler.metadata.Sieve;
import org.symqle.modeler.metadata.TableJavaNameAppender;
import org.symqle.modeler.sql.DatabaseObjectModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.transformer.Filter;
import org.symqle.modeler.transformer.FilterOutcome;
import org.symqle.modeler.transformer.RegexpFilter;
import org.symqle.modeler.transformer.Transformer;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public class PlainKeysTableTest extends TestCase {

    private final List<Transformer> transformers = Arrays.<Transformer>asList(createSieve(), new TableJavaNameAppender(), new ColumnJavaNameAppender(), new ForeignKeyJavaNameAppender());

    private final Sieve createSieve() {
        final Sieve sieve = new Sieve();
        final RegexpFilter regexpFilter = new RegexpFilter();
        regexpFilter.setPattern("SYS.*");
        regexpFilter.setMatchOutcome(FilterOutcome.DENY);
        regexpFilter.setProperty("TABLE_NAME");
        final Filter acceptOthers = new Filter() {
            @Override
            public FilterOutcome decide(final DatabaseObjectModel subject) {
                return FilterOutcome.ACCEPT;
            }
        };
        sieve.setTableFilters(Arrays.asList((Filter)regexpFilter, acceptOthers));
        return sieve;
    }

    public void testAllTypesTable() throws Exception {
        generate("freemarker/PlainKeysTable.ftl", "");
    }

    public void testAllTypesDto() throws Exception {
        generate("freemarker/PlainKeysDto.ftl", "Dto");
    }

    public void testAllTypesDao() throws Exception {
        generate("freemarker/PlainKeysDao.ftl", "Dao");
    }
    private void generate(final String templateName, final String suffix) throws Exception {
        final String url = "jdbc:derby:memory:symqle"+suffix;
        initDatabase(url, "PlainKeysTable.sql");
        final DataSource dataSource = new SingleConnectionDataSource(url, false);
        final MetadataReader reader = new MetadataReader();
        reader.setDataSource(dataSource);

        final SchemaSqlModel model = reader.readModel();
        SchemaSqlModel transformed = model;

        for (Transformer transformer : transformers) {
            transformed = transformer.transform(transformed);
        }
        final File packageDir = new File("target/test-generation/org/symqle/model");
        packageDir.mkdirs();
        final Map<String, String> packageNames = new HashMap<>();
        packageNames.put("model", "org.symqle.model");
        packageNames.put("dto", "org.symqle.model");
        packageNames.put("dao", "org.symqle.model");
        final FreeMarkerClassWriter writer = new FreeMarkerClassWriter();
        writer.setTemplateName(templateName);
        writer.setSuffix(suffix);
        for (TableSqlModel table : transformed.getTables()) {
            writer.writeClass(packageDir, "model", table, packageNames);
        }
    }

    protected void initDatabase(final String url, String resource) throws Exception {
        final DataSource dataSource = new SingleConnectionDataSource(url+";create=true", false);
        try (Connection connection = dataSource.getConnection()) {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resource)));
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
