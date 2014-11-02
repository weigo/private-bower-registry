/**
 *
 */
package org.arachna.bower.registry.impl;

import java.util.Collection;
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
    private final ConcurrentMap<String, BowerPackage> packages = new ConcurrentHashMap<String, BowerPackage>();

    @Override
    public Collection<BowerPackage> getAllPackages() {
        return packages.values();
    }

    @Override
    public void register(final BowerPackage bowerPackage) {
        if (packages.putIfAbsent(bowerPackage.getName(), bowerPackage) != null) {
            throw new IllegalArgumentException(String.format("package '%s' was already registered.", bowerPackage.getName()));
        }
    }

    @Override
    public BowerPackage getPackage(final String packageName) {
        return packages.get(packageName);
    }

    @Override
    public Collection<BowerPackage> search(final String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return getAllPackages();
        }

        return null;
    }
}
