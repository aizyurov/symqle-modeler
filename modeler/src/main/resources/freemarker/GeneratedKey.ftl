<#ftl strip_whitespace="true">
<#if model.primaryKey??>
<#list model.primaryKey.columns as column>
  <#if column.properties.GENERATED_KEY??>
/* THIS IS GENERATED CODE - ALL CHANGES WILL BE LOST */
package ${package};

import org.symqle.common.InBox;
import org.symqle.common.Mapper;
import org.symqle.common.OutBox;

import java.sql.SQLException;

<#assign javaType>${column.properties.GENERATED_KEY}</#assign>
/**
 * Unique key identifying ${column.owner.properties.JAVA_NAME}.
 */
public final class ${javaType} {
    private long id;
    private ${column.properties.GENERATED_KEY}(final long id) {
        this.id = id;
    }

  /**
   * Mapper for DepartmentId.
   */
    public static final Mapper<${javaType}> MAPPER = new Mapper<${javaType}>() {
        @Override
        public ${javaType} value(final InBox inBox) throws SQLException {
            final Long longId = inBox.getLong();
            return longId == null ? null : new ${javaType}(longId);
        }

        @Override
        public void setValue(final OutBox param, final ${javaType} value) throws SQLException {
            final Long longId = value == null ? null : value.id;
            param.setLong(longId);
        }
    };

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ${javaType} personId = (${javaType}) o;

        if (id != personId.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    private final static String prefix = ${javaType}.class.getName() + "#";

    @Override
    public final String toString() {
        return prefix + id;
    }

  /**
   * Creates ${javaType} from its string representation.
   * It is guaranteed that for any non null {@code id}
   * {@code ${javaType}.valueOf(id.toString()).equals(id)}
   * @param s source string
   * @return new instance of EmployeeId; null if s is null or contains only whitespace.
   * @throws IllegalArgumentException id is not null or empty and has format different from that produced by {@link #toString()}.
   */
    public static ${javaType} valueOf(final String s) {
        if (s == null  || s.trim().isEmpty()) {
            return null;
        }
        if (!s.startsWith(prefix)) {
            throw new IllegalArgumentException(s);
        }
        try {
            return new ${javaType}(Long.parseLong(s.substring(prefix.length())));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(s);
        }
    }

}
  </#if>
</#list>
</#if>