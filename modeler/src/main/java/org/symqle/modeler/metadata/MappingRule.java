package org.symqle.modeler.metadata;

/**
 * @author lvovich
 */
public class MappingRule {

    private String expression;
    private String mapper;
    private String javaType;

    public String getExpression() {
        return expression;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }

    public String getMapper() {
        return mapper;
    }

    public void setMapper(final String mapper) {
        this.mapper = mapper;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(final String javaType) {
        this.javaType = javaType;
    }
}
