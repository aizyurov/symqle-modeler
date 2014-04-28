package org.symqle.modeler;

import org.symqle.modeler.metadata.ColumnTransformer;
import org.symqle.modeler.metadata.ForeignKeyTransformer;
import org.symqle.modeler.metadata.GeneratedFkTransformer;
import org.symqle.modeler.metadata.MetadataReader;
import org.symqle.modeler.metadata.Sieve;
import org.symqle.modeler.metadata.TableTransformer;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.DatabaseObjectModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.transformer.Filter;
import org.symqle.modeler.transformer.RejectRegexpFilter;
import org.symqle.modeler.transformer.Transformer;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author lvovich
 */
public class TransformerTest extends DatabaseTestBase {

    private final List<Transformer> transformers = Arrays.<Transformer>asList(createSieve(), new TableTransformer(),
            new ColumnTransformer(), new GeneratedFkTransformer(), new ForeignKeyTransformer());

    private final Sieve createSieve() {
        final Sieve sieve = new Sieve();
        final RejectRegexpFilter regexpFilter = new RejectRegexpFilter();
        regexpFilter.setPattern("SYS.*");
        regexpFilter.setProperty("TABLE_NAME");
        final Filter acceptOthers = new Filter() {
            @Override
            public boolean accept(final DatabaseObjectModel subject) {
                return true;
            }
        };
        sieve.setTableFilters(Arrays.asList((Filter)regexpFilter, acceptOthers));
        return sieve;
    }


    public void testTransformers() throws Exception {
        final MetadataReader reader = new MetadataReader();
        final DataSource dataSource = getDataSource();

        reader.setDataSource(dataSource);

        final SchemaSqlModel model = reader.readModel();

        SchemaSqlModel transformed = model;

        for (Transformer transformer : transformers) {
            transformed = transformer.transform(transformed);
        }

        final Map<String, TableSqlModel> tableMap = new TreeMap<>();
        for (TableSqlModel table : transformed.getTables()) {
            final String name = table.getProperties().get("TABLE_NAME").toUpperCase();
            assertNull(name, tableMap.put(name, table));
        }
        assertTrue(tableMap.keySet().toString(), tableMap.keySet().containsAll(Arrays.asList("ALL_TYPES", "DEPARTMENT", "DETAIL", "EMPLOYEE", "MASTER")));
        final TableSqlModel allTypes = tableMap.get("ALL_TYPES");
        assertEquals("AllTypes", allTypes.getProperties().get("JAVA_NAME"));

        final ColumnSqlModel tVarchar = getColumnByName(allTypes, "T_VARCHAR");
        assertEquals("String", tVarchar.getProperties().get("JAVA_CLASS"));
        assertEquals("Mappers.STRING", tVarchar.getProperties().get("COLUMN_MAPPER"));

        final TableSqlModel employee = tableMap.get("EMPLOYEE");
        final ColumnSqlModel empId = getColumnByName(employee, "EMP_ID");
        assertEquals("EmployeeId", empId.getProperties().get("JAVA_CLASS"));
        assertEquals("EmployeeId.MAPPER", empId.getProperties().get("COLUMN_MAPPER"));
        assertEquals("EmployeeId", empId.getProperties().get("GENERATED_KEY"));
        final ColumnSqlModel deptId = getColumnByName(employee, "DEPT_ID");
        assertEquals("DepartmentId", deptId.getProperties().get("JAVA_CLASS"));
        assertEquals("DepartmentId.MAPPER", deptId.getProperties().get("COLUMN_MAPPER"));
        assertEquals("DepartmentId", deptId.getProperties().get("GENERATED_KEY"));

    }

    private ColumnSqlModel getColumnByName(final TableSqlModel allTypes, final String columnName) {
        final Map<String, ColumnSqlModel> columnMap = new HashMap<>();
        for (ColumnSqlModel column : allTypes.getColumns()) {
            columnMap.put(column.getProperties().get("COLUMN_NAME").toUpperCase(), column);
        }
        return columnMap.get(columnName);
    }

}