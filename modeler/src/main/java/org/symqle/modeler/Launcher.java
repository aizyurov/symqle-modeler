package org.symqle.modeler;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
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
        BeanDefinition configurer = (BeanDefinition) appContext.getBeanDefinition("propertyPlaceholderConfigurer");
        final MutablePropertyValues propertyValues = configurer.getPropertyValues();
        propertyValues.addPropertyValue("properties", localProperties);
        propertyValues.addPropertyValue("localOverride", true);
        appContext.refresh();

        final Generator generator = (Generator) appContext.getBean("generator");
        generator.generate();
    }

}
