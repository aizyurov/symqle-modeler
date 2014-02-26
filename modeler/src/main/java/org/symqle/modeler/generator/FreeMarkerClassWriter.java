package org.symqle.modeler.generator;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Required;
import org.symqle.modeler.sql.TableSqlModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvovich
 */
public class FreeMarkerClassWriter implements ClassWriter {

    private String templateName;
    private String suffix = "";

    @Required
    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    @Override
    public void writeClass(final File packageDir, final String packageKey, final TableSqlModel model, final Map<String, String> packageNames) throws IOException {
        final String className = model.getProperties().get("JAVA_NAME") + suffix;
        File generated = new File(packageDir, className + ".java");
        try (Writer writer = new FileWriter(generated)) {
            final Configuration configuration= new Configuration();
            configuration.setClassForTemplateLoading(this.getClass(), "/");
            configuration.setObjectWrapper(new DefaultObjectWrapper());
            final Template template = configuration.getTemplate(templateName);
            template.setObjectWrapper(new DefaultObjectWrapper());
            final Map<String, Object> root = new HashMap<>();
            root.put("package", packageKey);
            root.put("packages", packageNames);
            root.put("model", model);
            root.put("className", className);
            template.process(root, writer);
        } catch (TemplateException e) {
            throw new RuntimeException("Bad template", e);
        }
    }
}
