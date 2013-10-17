package org.symqle.modeler.transformer;

import org.symqle.modeler.sql.SchemaSqlModel;

/**
 * @author lvovich
 */
public interface Transformer {

    SchemaSqlModel transform(SchemaSqlModel source);
}
