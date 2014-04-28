package org.symqle.modeler;

import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.symqle.modeler.generator.Generator;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;

/**
 * @author lvovich
 */
public class BeanTest extends AbstractDependencyInjectionSpringContextTests {

    protected DataSource dataSource;

    protected Generator generator;

    private final String url = "jdbc:derby:memory:symqle-beans";

    public BeanTest() {
        setPopulateProtectedVariables(true);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] {"test-config.xml"};
    }

    @Override
    protected void onSetUp() throws Exception {
        FileUtils.deleteDirectory(new File("target/bean-test"));
        final DataSource initDataSource = new SingleConnectionDataSource(url+";create=true", false);
        DatabaseTestBase.initDatabase(initDataSource, "defaultDbSetup.sql");
    }

    @Override
    protected void onTearDown() throws Exception {
        final DataSource initDataSource = new SingleConnectionDataSource(url+";shutdown=true", false);
        try {
            initDataSource.getConnection();
        } catch (SQLException e) {
            // always; ignore
        }

    }

    public void test() throws Exception {
        generator.generate();
        final File dataDir = new File("target/bean-test/main/org/symqle/modeler/test/data");
        assertTrue(new File(dataDir, "DepartmentId.java").exists());
        assertEquals(2, dataDir.listFiles().length);

        final File modelDir = new File("target/bean-test/main/org/symqle/modeler/test/model");
        assertTrue(new File(modelDir, "AllTypes.java").exists());
        assertEquals(5, modelDir.listFiles().length);

        final File sampleDataDir = new File("target/bean-test/samples/org/symqle/modeler/test/data");
        assertTrue(new File(sampleDataDir, "AllTypesDto.java").exists());
        assertEquals(5, sampleDataDir.listFiles().length);

        final File daoDir = new File("target/bean-test/samples/org/symqle/modeler/test/dao");
        assertTrue(new File(daoDir, "AllTypesCrud.java").exists());
        assertTrue(new File(daoDir, "AllTypesSelect.java").exists());
        assertTrue(new File(daoDir, "AllTypesSmartSelect.java").exists());
        assertEquals(14, daoDir.listFiles().length);
    }
}
