package org.symqle.modeler.generator;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Required;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.utils.SimpleLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public abstract class FreeMarkerClassWriter implements ClassWriter {

    private String outputDirectory;
    private String packageKey;
    private String templateName;
    private String suffix = "";

    @Required
    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }


    public void setPackageKey(String packageKey) {
        this.packageKey = packageKey;
    }

    public void writeClasses(final SchemaSqlModel model,  final Map<String, String> packageNames)
                                                    throws IOException {
        final File outputDir = new File(outputDirectory);
        String packageName = packageNames.get(packageKey);
        final String packageSubdirName = packageName.replaceAll("\\.", "/");
        final File packageDir = new File(outputDir, packageSubdirName);
        packageDir.mkdirs();
        SimpleLogger.info("Writing classes to %s", packageDir);
        final List<TableSqlModel> tables = model.getTables();
        int count = 0;
        for (final TableSqlModel table : tables) {
            if (mustGenerate(table)) {
                final String className = table.getProperties().get("JAVA_NAME") + suffix;
                final Map<String, Object> root = new HashMap<>();
                root.put("package", packageNames.get(packageKey));
                root.put("packages", packageNames);
                root.put("model", table);
                root.put("className", className);
                File generated = new File(packageDir, className + ".java");
                writeFile(generated, root);
                SimpleLogger.debug("Generated: " + generated);
                count++;
            }
        }
        SimpleLogger.info("%s: %d classes generated", packageDir, count);
    }

    private void writeFile(final File file, final Map<String, Object> model) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            final Configuration configuration= new Configuration();
            configuration.setClassForTemplateLoading(this.getClass(), "/");
            configuration.setObjectWrapper(new DefaultObjectWrapper());
            final Template template = configuration.getTemplate(templateName);
            template.setObjectWrapper(new DefaultObjectWrapper());
            template.process(model, writer);
        } catch (TemplateException e) {
            throw new RuntimeException("Bad template", e);
        }
    }

    protected abstract boolean mustGenerate(TableSqlModel table);

}
