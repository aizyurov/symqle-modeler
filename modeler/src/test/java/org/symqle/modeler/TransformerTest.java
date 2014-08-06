package org.symqle.modeler;

import org.symqle.modeler.metadata.*;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.transformer.AllFilter;
import org.symqle.modeler.transformer.Filter;
import org.symqle.modeler.transformer.RegexpFilter;
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


    private final List<Transformer> transformers = Arrays.<Transformer>asList(createTableSieve(), new TableTransformer(),
            new ColumnTransformer(), new GeneratedFkTransformer(), new ForeignKeyTransformer());

    private TableSieve createTableSieve() {
        final TableSieve sieve = new TableSieve();
        final RegexpFilter regexpFilter = new RegexpFilter();
        regexpFilter.setPattern("SYS.*");
        regexpFilter.setProperty("TABLE_NAME");
        sieve.setTableFilters(Arrays.<Filter>asList(regexpFilter));
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

    public void testColumnSieve() throws Exception {
        final RegexpFilter tableFilter = new RegexpFilter();
        tableFilter.setProperty("TABLE_NAME");
        tableFilter.setPattern("DEPARTMENT");
        final RegexpFilter columnFilter = new RegexpFilter();
        columnFilter.setProperty("COLUMN_NAME");
        columnFilter.setPattern("MANAGER_ID");
        final AllFilter allFilter = new AllFilter();
        allFilter.setFilters(Arrays.<Filter>asList(tableFilter, columnFilter));
        final ColumnSieve columnSieve = new ColumnSieve();
        columnSieve.setColumnFilters(Arrays.<Filter>asList(allFilter));
        final MetadataReader reader = new MetadataReader();
        final DataSource dataSource = getDataSource();

        reader.setDataSource(dataSource);

        final SchemaSqlModel model = reader.readModel();
        final SchemaSqlModel transformed = columnSieve.transform(model);
        for (TableSqlModel table : transformed.getTables()) {
            if (table.getProperties().get("TABLE_NAME").equals("EMPLOYEE")) {
                assertEquals(8, table.getColumns().size());
            } else if (table.getProperties().get("TABLE_NAME").equals("DEPARTMENT")) {
                assertEquals(3, table.getColumns().size());
                for (final ColumnSqlModel column : table.getColumns()) {
                    assertFalse(column.getProperties().toString(), column.getProperties().get("COLUMN_NAME").equals("MANAGER_ID"));
                }
                assertEquals(1, table.getForeignKeys().size());
                final ForeignKeySqlModel fk = table.getForeignKeys().get(0);
                assertFalse(fk.getProperties().get("FK_NAME").equals("MANAGER_FK"));
            }
        }


    }

    public void testPrimaryKeyColumnSieve() throws Exception {
        final RegexpFilter tableFilter = new RegexpFilter();
        tableFilter.setProperty("TABLE_NAME");
        tableFilter.setPattern("DEPARTMENT");
        final RegexpFilter columnFilter = new RegexpFilter();
        columnFilter.setProperty("COLUMN_NAME");
        columnFilter.setPattern("DEPT_ID");
        final AllFilter allFilter = new AllFilter();
        allFilter.setFilters(Arrays.<Filter>asList(tableFilter, columnFilter));
        final ColumnSieve columnSieve = new ColumnSieve();
        columnSieve.setColumnFilters(Arrays.<Filter>asList(allFilter));
        final MetadataReader reader = new MetadataReader();
        final DataSource dataSource = getDataSource();

        reader.setDataSource(dataSource);

        final SchemaSqlModel model = reader.readModel();
        final SchemaSqlModel transformed = columnSieve.transform(model);
        for (TableSqlModel table : transformed.getTables()) {
            if (table.getProperties().get("TABLE_NAME").equals("EMPLOYEE")) {
                // dept_id must not be hidden
                assertEquals(8, table.getColumns().size());
                // fk to department.dept_id MUST be hidden
                assertEquals(0, table.getForeignKeys().size());
            } else if (table.getProperties().get("TABLE_NAME").equals("DEPARTMENT")) {
                assertEquals(3, table.getColumns().size());
                for (final ColumnSqlModel column : table.getColumns()) {
                    assertFalse(column.getProperties().toString(), column.getProperties().get("COLUMN_NAME").equals("DEPT_ID"));
                }
                assertEquals(1, table.getForeignKeys().size());
                final ForeignKeySqlModel fk = table.getForeignKeys().get(0);
                assertFalse(fk.getProperties().get("FK_NAME").equals("PARENT_FK"));
                assertNull(table.getPrimaryKey());
            }
        }
    }

    public void testForeignKeySieve() throws Exception {
        final RegexpFilter fkFilter = new RegexpFilter();
        fkFilter.setProperty("FK_NAME");
        fkFilter.setPattern("PARENT_FK");
        final ForeignKeySieve columnSieve = new ForeignKeySieve();
        columnSieve.setForeignKeyFilters(Arrays.<Filter>asList(fkFilter));
        final MetadataReader reader = new MetadataReader();
        final DataSource dataSource = getDataSource();

        reader.setDataSource(dataSource);

        final SchemaSqlModel model = reader.readModel();
        final SchemaSqlModel transformed = columnSieve.transform(model);
        for (TableSqlModel table : transformed.getTables()) {
            if (table.getProperties().get("TABLE_NAME").equals("EMPLOYEE")) {
                // everything should be in place
                assertEquals(8, table.getColumns().size());
                // fk to department.dept_id MUST be hidden
                assertEquals(1, table.getForeignKeys().size());
            } else if (table.getProperties().get("TABLE_NAME").equals("DEPARTMENT")) {
                // all columns kept
                assertEquals(4, table.getColumns().size());
                assertEquals(1, table.getForeignKeys().size());
                final ForeignKeySqlModel fk = table.getForeignKeys().get(0);
                assertFalse(fk.getProperties().get("FK_NAME").equals("PARENT_FK"));
            }
        }
    }

    public void testFilterOutTable() throws Exception {
        final TableSieve sieve = new TableSieve();
        final RegexpFilter regexpFilter = new RegexpFilter();
        regexpFilter.setPattern("EMPLOYEE");
        regexpFilter.setProperty("TABLE_NAME");
        sieve.setTableFilters(Arrays.<Filter>asList(regexpFilter));
        final MetadataReader reader = new MetadataReader();
        final DataSource dataSource = getDataSource();

        reader.setDataSource(dataSource);

        final SchemaSqlModel model = reader.readModel();
        final SchemaSqlModel transformed = sieve.transform(model);
        for (TableSqlModel table : transformed.getTables()) {
            if (table.getProperties().get("TABLE_NAME").equals("EMPLOYEE")) {
                fail("EMPLOYEE should be deleted");
            } else if (table.getProperties().get("TABLE_NAME").equals("DEPARTMENT")) {
                // all columns kept
                assertEquals(4, table.getColumns().size());
                assertEquals(1, table.getForeignKeys().size());
                // manager_fk must be lost because it points to deleted EMPLOYEE
                final ForeignKeySqlModel fk = table.getForeignKeys().get(0);
                assertFalse(fk.getProperties().get("FK_NAME").equals("MANAGER_FK"));
            }
        }


    }

    private ColumnSqlModel getColumnByName(final TableSqlModel allTypes, final String columnName) {
        final Map<String, ColumnSqlModel> columnMap = new HashMap<>();
        for (ColumnSqlModel column : allTypes.getColumns()) {
            columnMap.put(column.getProperties().get("COLUMN_NAME").toUpperCase(), column);
        }
        return columnMap.get(columnName);
    }

}