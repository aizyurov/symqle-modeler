package org.symqle.modeler;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;

/**
 * @author lvovich
 */
public class MainTest extends TestCase {

    private final String url = "jdbc:derby:memory:symqle-beans";

    @Override
    protected void setUp() throws Exception {
        FileUtils.deleteDirectory(new File("target/bean-test"));
        final DataSource initDataSource = new SingleConnectionDataSource(url+";create=true", false);
        DatabaseTestBase.initDatabase(initDataSource, "defaultDbSetup.sql");
    }

    @Override
    protected void tearDown() throws Exception {
        final DataSource initDataSource = new SingleConnectionDataSource(url+";drop=true", false);
        try {
            initDataSource.getConnection();
        } catch (SQLException e) {
            // always; ignore
        }
    }


    public void testMain() throws Exception {
        new Main(new String[] {"-c", "src/test/resources/test.modeler.properties"}).run();
        final File dataDir = new File("target/bean-test/main/org/symqle/modeler/test/data");
        assertTrue(new File(dataDir, "DepartmentId.java").exists());
        assertEquals(4, dataDir.listFiles().length);

        final File modelDir = new File("target/bean-test/main/org/symqle/modeler/test/model");
        assertTrue(new File(modelDir, "AllTypes.java").exists());
        assertEquals(11, modelDir.listFiles().length);

        final File sampleDataDir = new File("target/bean-test/samples/org/symqle/modeler/test/data");
        assertTrue(new File(sampleDataDir, "AllTypesDto.java").exists());
        assertEquals(11, sampleDataDir.listFiles().length);

        final File daoDir = new File("target/bean-test/samples/org/symqle/modeler/test/dao");
        assertTrue(new File(daoDir, "AllTypesCrud.java").exists());
        assertTrue(new File(daoDir, "AllTypesSelect.java").exists());
        assertTrue(new File(daoDir, "AllTypesSmartSelect.java").exists());
        assertEquals(31, daoDir.listFiles().length);
    }
}
