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
        new CommandLineRunner(new String[] {"-c", "src/test/resources/test.modeler.properties"}).run();
        final File dataDir = new File("target/bean-test/main/org/symqle/modeler/test/data");
        assertTrue(new File(dataDir, "DepartmentId.java").exists());
        assertEquals(4, dataDir.listFiles().length);

        final File modelDir = new File("target/bean-test/main/org/symqle/modeler/test/model");
        assertTrue(new File(modelDir, "AllTypes.java").exists());
        assertTrue(new File(modelDir, "Plain1.java").exists());
        assertEquals(12, modelDir.listFiles().length);

        final File sampleDataDir = new File("target/bean-test/samples/org/symqle/modeler/test/data");
        assertTrue(new File(sampleDataDir, "AllTypesDto.java").exists());
        assertEquals(12, sampleDataDir.listFiles().length);

        final File daoDir = new File("target/bean-test/samples/org/symqle/modeler/test/dao");
        assertTrue(new File(daoDir, "AllTypesCrud.java").exists());
        assertTrue(new File(daoDir, "AllTypesSelect.java").exists());
        assertTrue(new File(daoDir, "AllTypesSmartSelect.java").exists());
        assertEquals(33, daoDir.listFiles().length);
    }

    public void testWrongOptions() throws Exception {
        final int retval = new CommandLineRunner(new String[]{"-a -b -c"}).run();
        assertEquals(1, retval);
    }

    public void testMissingConfigName() throws Exception {
        final int retval = new CommandLineRunner(new String[]{"-c"}).run();
        assertEquals(1, retval);
    }

    public void testMissingConfigFile() throws Exception {
        final int retval = new CommandLineRunner(new String[]{"-c abrakadabra"}).run();
        assertEquals(1, retval);
    }

    public void testMissingProperty() throws Exception {
        final int retval = new CommandLineRunner(new String[]{"-c", "src/test/resources/broken.properties", "-v"}).run();
        assertEquals(1, retval);
    }

    public void testVerboseOption() throws Exception {
        final int retval = new CommandLineRunner(new String[] {"-c", "src/test/resources/test.modeler.properties", "-v"}).run();
        assertEquals(0, retval);
        final File dataDir = new File("target/bean-test/main/org/symqle/modeler/test/data");
        assertTrue(new File(dataDir, "DepartmentId.java").exists());
        assertEquals(4, dataDir.listFiles().length);
    }

    public void testWithClasspath() throws Exception {
        final int retval = new CommandLineRunner(new String[] {"-c", "src/test/resources/test.modeler.withclasspath.properties", "-v"}).run();
        assertEquals(0, retval);
        final File dataDir = new File("target/bean-test/main/org/symqle/modeler/test/data");
        assertTrue(new File(dataDir, "DepartmentId.java").exists());
        assertEquals(4, dataDir.listFiles().length);
    }

}


