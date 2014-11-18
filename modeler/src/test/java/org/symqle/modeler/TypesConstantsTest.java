package org.symqle.modeler;

import junit.framework.TestCase;
import org.symqle.modeler.metadata.TypesConstants;

import java.sql.Types;

/**
 * @author lvovich
 */
public class TypesConstantsTest extends TestCase {

    public void testTypes() throws Exception {
        final String varchar = TypesConstants.getTypeName(Types.VARCHAR);
        assertEquals("VARCHAR", varchar);
        final String numeric = TypesConstants.getTypeName(Types.NUMERIC);
        assertEquals("NUMERIC", numeric);
    }
}
