package org.symqle.modeler.generator;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.Writer;

/**
 * A wrapper around Freemarker Template.
 * It expects that model does not contain any errors and is consistent with template.
 * @author lvovich
 */
public class TemplateWrapper {

    private final Template template;

    public TemplateWrapper(final Template template) {
        this.template = template;
    }

    /**
     * Unlike {@link freemarker.template.Template#process(Object, java.io.Writer)} does not throw TemplateException.
     * Throws IllegalArgumentException in case of template error.
     * @param rootModel
     * @param writer
     * @throws IOException
     */
    public void process(final Object rootModel, final Writer writer) throws IOException {
        try {
            template.process(rootModel, writer);
        } catch (TemplateException e) {
            throw new IllegalArgumentException("Template processing problem", e);
        }
    }
}
