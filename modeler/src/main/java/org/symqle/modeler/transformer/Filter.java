package org.symqle.modeler.transformer;

import org.symqle.modeler.sql.DatabaseObjectModel;

/**
 * @author lvovich
 */
public interface Filter {

    boolean reject(DatabaseObjectModel subject);

}
