package org.symqle.modeler;

import junit.framework.TestCase;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.symqle.modeler.metadata.MetadataReader;
import org.symqle.modeler.sql.ColumnPair;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

import javax.sql.DataSource;

/**
 * @author lvovich
 */
public class MetadataReaderTest extends TestCase {

    public void testSymqleTestDatabase() throws Exception {
        final MetadataReader reader = new MetadataReader();
        final DataSource dataSource = new SingleConnectionDataSource(
                "com.mysql.jdbc.Driver",
                "jdbc:mysql://localhost:3306/jtrac",
                "simqle",
                "simqle",
                false);

        reader.setDataSource(dataSource);

        final SchemaSqlModel model = reader.readModel();

        for (TableSqlModel table : model.getTables()) {
                System.out.println("Table: " + table.getProperties().get("TABLE_NAME") + "[" +table.getProperties().get("TABLE_TYPE") +"]");
                for (ColumnSqlModel columnSqlModel: table.getColumns()) {
                    System.out.println("    " + columnSqlModel.getProperties().get("COLUMN_NAME") + ":" + columnSqlModel.getProperties().get("TYPE_NAME") + " (nullability: "+columnSqlModel.getProperties().get("NULLABLE") +")" + "(size: " + columnSqlModel.getProperties().get("COLUMN_SIZE") +")");
                }

                for (ForeignKeySqlModel fk : table.getForeignKeys()) {
                    System.out.print("fk:" + fk.getProperties().get("FK_NAME") + " = ");
                    for (ColumnPair pair : fk.getMapping()) {
                        System.out.print(pair.getFirst().getProperties().get("COLUMN_NAME") + "->" + pair.getSecond().getProperties().get("TABLE_NAME") + "." + pair.getSecond().getProperties().get("COLUMN_NAME") +" ");
                    }
                    System.out.println(" NOT_NULLABLE:" + fk.getProperties().get("NOT_NULLABLE"));
                }
            }
    }
}
