package org.symqle.modeler;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.symqle.modeler.generator.Generator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Common entry point for java clients
 * @author lvovich
 */
public class Launcher {

    /**
     * Runs source generation
     * @param localProperties configuration properties to augment/override defaults
     * @throws IOException
     * @throws SQLException
     * @throws ReflectiveOperationException
     */
    public void run(final Properties localProperties) throws IOException, SQLException, ReflectiveOperationException {

        final GenericApplicationContext appContext = new GenericApplicationContext();
        final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(appContext);
        xmlReader.loadBeanDefinitions(appContext.getResource("config.xml"));
        PropertyPlaceholderConfigurer configurer = (PropertyPlaceholderConfigurer) appContext.getBean("propertyPlaceholderConfigurer");
        configurer.setLocalOverride(true);
        configurer.setProperties(localProperties);
        appContext.addBeanFactoryPostProcessor(configurer);
        appContext.refresh();

        final Generator generator = (Generator) appContext.getBean("generator");
        generator.generate();
    }

}
