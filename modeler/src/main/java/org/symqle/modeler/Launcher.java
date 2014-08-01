package org.symqle.modeler;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.symqle.modeler.generator.Generator;
import org.symqle.modeler.utils.SimpleLogger;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Common entry point for java clients
 * @author lvovich
 */
public class Launcher {

    private final static String[] requiredProperties = {
                    "outputDirectory",
                    "samplesDirectory",
                    "jdbcDriver",
                    "jdbcUrl",
                    "jdbcUser",
                    "dataPackage",
                    "dataAccessPackage",
                    "symqleModelPackage"
            };

    /**
     * Runs source generation
     * @param localProperties configuration properties to augment/override defaults
     * @throws IOException
     * @throws SQLException
     * @throws ReflectiveOperationException
     */
    public int run(final Properties localProperties) throws IOException, SQLException, ReflectiveOperationException {


        for (String key : requiredProperties) {
            if (!localProperties.containsKey(key)) {
                SimpleLogger.error("Missing required property: %s", key);
                return 1;
            }
        }

        final String classPathValue = localProperties.getProperty("classpath");
        if (classPathValue != null) {
            SimpleLogger.info("Setting classpath: %s", classPathValue);
            final String[] classPathElements = classPathValue.split(":");
            final URL[] classPath = new URL[classPathElements.length];
            for (int i = 0; i < classPathElements.length; i++) {
                classPath[i] = new URL("file://"+classPathElements[i]);
            }
            final URLClassLoader urlClassLoader = new URLClassLoader(classPath);
            Thread.currentThread().setContextClassLoader(urlClassLoader);
        } else {
            SimpleLogger.warn("No classpath property in config file; jdbc drivers may be unavailable");
        }

        final GenericApplicationContext appContext = new GenericApplicationContext();
        final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(appContext);
        xmlReader.loadBeanDefinitions(appContext.getResource("classpath:config.xml"));
        BeanDefinition configurer = appContext.getBeanDefinition("propertyPlaceholderConfigurer");
        final MutablePropertyValues propertyValues = configurer.getPropertyValues();
        propertyValues.addPropertyValue("properties", localProperties);
        propertyValues.addPropertyValue("localOverride", true);
        appContext.refresh();

        final Generator generator = (Generator) appContext.getBean("generator");
        generator.generate();
        return 0;
    }

}
