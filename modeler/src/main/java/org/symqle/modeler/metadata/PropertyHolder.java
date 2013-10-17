package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.DatabaseObjectModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
* @author lvovich
*/
public class PropertyHolder implements DatabaseObjectModel {
    final Map<String, String> properties = new HashMap<>();

    PropertyHolder(final Map<String, String> properties) {
        this.properties.putAll(properties);
    }

    public PropertyHolder(final DatabaseObjectModel other) {
            properties.putAll(other.getProperties());
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
}
