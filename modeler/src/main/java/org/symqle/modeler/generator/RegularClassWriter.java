package org.symqle.modeler.generator;

import org.symqle.modeler.sql.TableSqlModel;

/**
 * Created by aizyurov on 4/26/14.
 */
public class RegularClassWriter extends FreeMarkerClassWriter {

    @Override
    protected boolean mustGenerate(TableSqlModel table) {
        return true;
    }
}
