package org.symqle.modeler;

import junit.framework.TestCase;
import org.symqle.modeler.metadata.ColumnJavaNameAppender;
import org.symqle.modeler.metadata.ForeignKeyJavaNameAppender;
import org.symqle.modeler.metadata.MetadataReader;
import org.symqle.modeler.metadata.TableJavaNameAppender;
import org.symqle.modeler.sql.ColumnPair;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.transformer.Transformer;

import java.util.Arrays;
import java.util.List;

/**
 * @author lvovich
 */
public class TransformerTest extends TestCase {

    private final List<Transformer> transformers = Arrays.<Transformer>asList(new TableJavaNameAppender(), new ColumnJavaNameAppender(), new ForeignKeyJavaNameAppender());

    public void testSymqleTestDatabase() throws Exception {
        final MetadataReader reader = new MetadataReader();
        reader.setDriverClassName("com.mysql.jdbc.Driver");
        reader.setDatabaseUrl("jdbc:mysql://localhost:3306/jtrac");
        reader.setUser("simqle");
        reader.setPassword("simqle");

        final SchemaSqlModel model = reader.readModel();

        SchemaSqlModel transformed = model;

        for (Transformer transformer : transformers) {
            transformed = transformer.transform(transformed);
        }

        for (TableSqlModel table : transformed.getTables()) {
                System.out.println("Table: " + table.getProperties().get("JAVA_NAME") + "[" +table.getProperties().get("TABLE_TYPE") +"]");
                for (ColumnSqlModel columnSqlModel: table.getColumns()) {
                    System.out.println("    " + columnSqlModel.getProperties().get("JAVA_NAME") + ":" + columnSqlModel.getProperties().get("TYPE_NAME") + " (nullability: "+columnSqlModel.getProperties().get("NULLABLE") +")" + "(size: " + columnSqlModel.getProperties().get("COLUMN_SIZE") +")");
                }

                for (ForeignKeySqlModel fk : table.getForeignKeys()) {
                    System.out.print("fk:" + fk.getProperties().get("JAVA_NAME") + " = ");
                    for (ColumnPair pair : fk.getMapping()) {
                        System.out.print(pair.getFirst().getProperties().get("JAVA_NAME") + "->" + pair.getSecond().getProperties().get("TABLE_NAME") + "." + pair.getSecond().getProperties().get("JAVA_NAME") +" ");
                    }
                    System.out.println(" NOT_NULLABLE:" + fk.getProperties().get("NOT_NULLABLE"));
                }
            }
    }

}
