package org.symqle.modeler.cli;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.symqle.modeler.DatabaseTestBase;

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
        Assert.assertTrue(new File(dataDir, "DepartmentId.java").exists());
        Assert.assertEquals(4, dataDir.listFiles().length);

        final File modelDir = new File("target/bean-test/main/org/symqle/modeler/test/model");
        Assert.assertTrue(new File(modelDir, "AllTypes.java").exists());
        Assert.assertTrue(new File(modelDir, "Plain1.java").exists());
        Assert.assertEquals(12, modelDir.listFiles().length);

        final File sampleDataDir = new File("target/bean-test/samples/org/symqle/modeler/test/data");
        Assert.assertTrue(new File(sampleDataDir, "AllTypesDto.java").exists());
        Assert.assertEquals(12, sampleDataDir.listFiles().length);

        final File daoDir = new File("target/bean-test/samples/org/symqle/modeler/test/dao");
        Assert.assertTrue(new File(daoDir, "AllTypesCrud.java").exists());
        Assert.assertTrue(new File(daoDir, "AllTypesSelect.java").exists());
        Assert.assertTrue(new File(daoDir, "AllTypesSmartSelect.java").exists());
        Assert.assertEquals(33, daoDir.listFiles().length);
    }

    public void testWrongOptions() throws Exception {
        final int retval = new CommandLineRunner(new String[]{"-a -b -c"}).run();
        Assert.assertEquals(1, retval);
    }

    public void testMissingConfigName() throws Exception {
        final int retval = new CommandLineRunner(new String[]{"-c"}).run();
        Assert.assertEquals(1, retval);
    }

    public void testMissingConfigFile() throws Exception {
        final int retval = new CommandLineRunner(new String[]{"-c abrakadabra"}).run();
        Assert.assertEquals(1, retval);
    }

    public void testMissingProperty() throws Exception {
        final int retval = new CommandLineRunner(new String[]{"-c", "src/test/resources/broken.properties", "-v"}).run();
        Assert.assertEquals(1, retval);
    }

    public void testVerboseOption() throws Exception {
        final int retval = new CommandLineRunner(new String[] {"-c", "src/test/resources/test.modeler.properties", "-v"}).run();
        Assert.assertEquals(0, retval);
        final File dataDir = new File("target/bean-test/main/org/symqle/modeler/test/data");
        Assert.assertTrue(new File(dataDir, "DepartmentId.java").exists());
        Assert.assertEquals(4, dataDir.listFiles().length);
    }

    public void testWithClasspath() throws Exception {
        final int retval = new CommandLineRunner(new String[] {"-c", "src/test/resources/test.modeler.withclasspath.properties", "-v"}).run();
        Assert.assertEquals(0, retval);
        final File dataDir = new File("target/bean-test/main/org/symqle/modeler/test/data");
        Assert.assertTrue(new File(dataDir, "DepartmentId.java").exists());
        Assert.assertEquals(4, dataDir.listFiles().length);
    }

}


