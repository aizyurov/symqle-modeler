package org.symqle.modeler;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.symqle.modeler.utils.RegularStdErrLogger;
import org.symqle.modeler.utils.SimpleLogger;
import org.symqle.modeler.utils.VerboseStdErrLogger;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author lvovich
 */
public class LauncherTest extends TestCase {

    private final String url = "jdbc:derby:memory:symqle-beans";

    @Override
    protected void setUp() throws Exception {
        FileUtils.deleteDirectory(new File("target/bean-test"));
        final DataSource dropDataSource = new SingleConnectionDataSource(url+";drop=true", false);
        try {
            dropDataSource.getConnection();
        } catch (SQLException e) {
            // always; ignore
        }
        final DataSource initDataSource = new SingleConnectionDataSource(url+";create=true", false);
        DatabaseTestBase.initDatabase(initDataSource, "defaultDbSetup.sql");
    }

    @Override
    protected void tearDown() throws Exception {
        final DataSource dropDataSource = new SingleConnectionDataSource(url+";drop=true", false);
        try {
            dropDataSource.getConnection();
        } catch (SQLException e) {
            // always; ignore
        }
    }

    public void testWithoutClasspath() throws Exception {

        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.modeler.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        SimpleLogger.setLogger(new VerboseStdErrLogger());
        final int status = new Launcher().run(properties);
        assertEquals(0, status);
        final File dataDir = new File("target/bean-test/main/org/symqle/modeler/test/data");
        Assert.assertTrue(new File(dataDir, "DepartmentId.java").exists());
        Assert.assertEquals(4, dataDir.listFiles().length);
        SimpleLogger.setLogger(new RegularStdErrLogger());
        final int status1 = new Launcher().run(properties);
        assertEquals(0, status1);

    }

    public void testWithClasspath() throws Exception {

        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.modeler.withclasspath.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        SimpleLogger.setLogger(new RegularStdErrLogger());
        new Launcher().run(properties);
        final File dataDir = new File("target/bean-test/main/org/symqle/modeler/test/data");
        Assert.assertTrue(new File(dataDir, "DepartmentId.java").exists());
        Assert.assertEquals(4, dataDir.listFiles().length);

    }

    public void testMissingProperties() throws Exception {

        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.modeler.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.remove("jdbcDriver");
        SimpleLogger.setLogger(new VerboseStdErrLogger());
        final int status = new Launcher().run(properties);
        assertEquals(1, status);

    }

    public void testMissingPropertiesRegularLogging() throws Exception {

        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.modeler.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.remove("jdbcDriver");
        SimpleLogger.setLogger(new RegularStdErrLogger());
        final int status = new Launcher().run(properties);
        assertEquals(1, status);

    }

}
