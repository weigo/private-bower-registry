/**
 *
 */
package org.arachna.bower.registry.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.arachna.bower.registry.BowerPackage;
import org.arachna.bower.registry.BowerRegistry;

/**
 * A map based implementation of a {@link BowerRegistry}.
 *
 * @author Dirk Weigenand
 */
public class BowerPackageMap implements BowerRegistry {
    /**
     * Map of registered bower packages.
     */
    // FIXME: use ehcache
    private final ConcurrentMap<String, BowerPackage> packages = new ConcurrentHashMap<String, BowerPackage>();

    @Override
    public Collection<BowerPackage> getAllPackages() {
        return Collections.unmodifiableCollection(packages.values());
    }

    @Override
    public void register(final BowerPackage bowerPackage) {
        packages.put(bowerPackage.getName(), bowerPackage);
        // if (packages.putIfAbsent(bowerPackage.getName(), bowerPackage) != null) {
        // throw new IllegalArgumentException(String.format("package '%s' was already registered.", bowerPackage.getName()));
        // }
    }

    @Override
    public BowerPackage getPackage(final String packageName) {
        return packages.get(packageName);
    }

    @Override
    public Collection<BowerPackage> search(final String packageName) {
        final Collection<BowerPackage> allPackages = getAllPackages();

        if (StringUtils.isEmpty(packageName)) {
            return allPackages;
        }

        final StringContainedInPackageNameFilter filter = new StringContainedInPackageNameFilter(packageName);
        final Collection<BowerPackage> matches = new LinkedList<BowerPackage>();

        for (final BowerPackage bowerPackage : allPackages) {
            if (filter.accept(bowerPackage)) {
                matches.add(bowerPackage);
            }
        }

        return matches;
    }
}
