/**
 *
 */
package org.arachna.bower.registry.impl;

import org.apache.commons.lang3.StringUtils;
import org.arachna.bower.registry.BowerPackage;
import org.arachna.bower.registry.BowerPackageFilter;

/**
 * Filter for matching {@link BowerPackage} instances against a (possibly partial) package name.
 *
 * @author Dirk Weigenand
 */
public class StringContainedInPackageNameFilter implements BowerPackageFilter {
    /**
     * (possibly partial) package name to match {@link BowerPackage} instances against.
     */
    private final String partialPackageName;

    /**
     * Create a filter instance using the given package name.
     *
     * @param partialPackageName
     *            (possibly partial) package name to match {@link BowerPackage} instances against.
     */
    public StringContainedInPackageNameFilter(final String partialPackageName) {
        if (StringUtils.isEmpty(partialPackageName)) {
            throw new IllegalArgumentException("package name for matching must not be null or empty!");
        }

        this.partialPackageName = partialPackageName;
    }

    @Override
    public boolean accept(final BowerPackage bowerPackage) {
        return bowerPackage != null && bowerPackage.getName().contains(partialPackageName);
    }
}
