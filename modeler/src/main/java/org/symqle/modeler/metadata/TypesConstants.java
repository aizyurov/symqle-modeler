package org.symqle.modeler.metadata;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvovich
 */
public class TypesConstants {
    private static Map<Integer, String> typeMapping = new HashMap<>();
    static {
        try {
            final Class<Types> typesClass = Types.class;
            final Field[] fields = typesClass.getFields();
            for (Field f : fields) {
                typeMapping.put(f.getInt(null), f.getName());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Internal error", e);
        }
    }

    public static String getTypeName(int typeId) {
        return typeMapping.get(typeId);
    }
}
