package org.symqle.modeler;

import org.symqle.modeler.metadata.MetadataReader;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author lvovich
 */
public class MetadataReaderTest extends DatabaseTestBase {

    public void testReader() throws Exception {
        final MetadataReader reader = new MetadataReader();
        final DataSource dataSource = getDataSource();

        reader.setDataSource(dataSource);

        final SchemaSqlModel model = reader.readModel();

        final Map<String, TableSqlModel> tableMap = new TreeMap<>();
        for (TableSqlModel table : model.getTables()) {
            final String name = table.getProperties().get("TABLE_NAME");
            assertNull(name, tableMap.put(name, table));
        }
        // may be also system tables
        assertTrue(tableMap.keySet().toString(), tableMap.keySet().containsAll(Arrays.asList("ALL_TYPES", "DEPARTMENT", "DETAIL", "EMPLOYEE", "MASTER")));
        final TableSqlModel allTypes = tableMap.get("ALL_TYPES");
        assertNotNull(allTypes.getPrimaryKey());
        assertEquals(1, allTypes.getPrimaryKey().getColumns().size());
        final ColumnSqlModel pkColumn = allTypes.getPrimaryKey().getColumns().get(0);
        assertEquals("T_BIT", pkColumn.getProperties().get("COLUMN_NAME"));
        assertEquals("NO", pkColumn.getProperties().get("IS_NULLABLE"));

        final Map<String, ColumnSqlModel> columnMap = new HashMap<>();
        for (ColumnSqlModel column : allTypes.getColumns()) {
            columnMap.put(column.getProperties().get("COLUMN_NAME"), column);
        }
        final ColumnSqlModel tVarchar = columnMap.get("T_VARCHAR");
        assertEquals("12", tVarchar.getProperties().get("DATA_TYPE"));
        assertEquals("20", tVarchar.getProperties().get("COLUMN_SIZE"));

        final TableSqlModel detail = tableMap.get("DETAIL");
        assertEquals(1, detail.getForeignKeys().size());
        final ForeignKeySqlModel fk = detail.getForeignKeys().get(0);
        assertEquals(tableMap.get("MASTER"), fk.getReferencedTable());
        assertEquals("MASTER_FK", fk.getProperties().get("FK_NAME"));
        assertEquals(2, fk.getMapping().size());
        assertEquals("MASTER_MAJOR", fk.getMapping().get(0).getFirst().getProperties().get("COLUMN_NAME"));
        assertEquals("MAJOR", fk.getMapping().get(0).getSecond().getProperties().get("COLUMN_NAME"));
        assertEquals("MASTER_MINOR", fk.getMapping().get(1).getFirst().getProperties().get("COLUMN_NAME"));
        assertEquals("MINOR", fk.getMapping().get(1).getSecond().getProperties().get("COLUMN_NAME"));

    }
}
