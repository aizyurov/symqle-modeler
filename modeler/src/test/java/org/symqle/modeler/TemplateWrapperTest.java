package org.symqle.modeler;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import junit.framework.TestCase;
import org.symqle.modeler.generator.TemplateWrapper;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author lvovich
 */
public class TemplateWrapperTest extends TestCase {

    public void testBrokenTemplate() throws Exception {
        final Configuration configuration= new Configuration();
        configuration.setClassForTemplateLoading(this.getClass(), "/");
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        final Template fmTemplate = configuration.getTemplate("brokenTemplate.ftl");
        fmTemplate.setObjectWrapper(new DefaultObjectWrapper());
        final StringWriter writer = new StringWriter();
        try {
            new TemplateWrapper(fmTemplate).process(new Object(), writer);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // fine
        }

    }
}
