package org.symqle.modeler;

import junit.framework.TestCase;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvovich
 */
public class TypesTest extends TestCase {

    public void testTypesConstants() throws Exception {
        final Class<Types> typesClass = Types.class;
        final Field[] fields = typesClass.getFields();
        for (Field f : fields) {
            System.out.println("map.put("+f.getInt(null) + ", \""+f.getName() +"\");");
        }
    }

    public void testExpressions() throws Exception {
            // Create or retrieve a JexlEngine
            JexlEngine jexl = new JexlEngine();
            // Create an expression object
            String jexlExp = "JAVA_TYPE_NAME == 'TINYINT' && LENGTH == 1";
            Expression e = jexl.createExpression( jexlExp );

            Map<String, String> properties = new HashMap<>();
        properties.put("JAVA_TYPE_NAME", "TINYINT");
//        properties.put("LENGTH", "1");

            // Create a context and add data
            JexlContext jc = new MapContext();
        for (Map.Entry<String, String> entry : properties.entrySet()){
            jc.set(entry.getKey(), entry.getValue());
        }

            // Now evaluate the expression, getting the result
            Object o = e.evaluate(jc);

        System.out.println(o);

    }
}
