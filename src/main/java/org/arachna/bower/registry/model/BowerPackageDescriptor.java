package org.arachna.bower.registry.model;

import org.arachna.bower.registry.BowerPackage;

/**
 * A bower package descriptor.
 *
 * @author Dirk Weigenand
 */
public class BowerPackageDescriptor implements BowerPackage {
    private final String name;
    private final String url;

    public BowerPackageDescriptor(final String name, final String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return url;
    }
}
