package org.symqle.modeler.generator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author lvovich
 */
public class Main {

    public static void main(String[] args) {
        try {
            final String beanLocation = System.getProperty("symqle.modeler.config", "classpath:config.xml");
            ApplicationContext context = new FileSystemXmlApplicationContext(beanLocation);
            final Generator generator = (Generator) context.getBean("generator");
            generator.generate();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(2);
        }
    }
}
