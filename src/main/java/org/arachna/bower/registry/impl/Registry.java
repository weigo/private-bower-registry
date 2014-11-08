/**
 * 
 */
package org.arachna.bower.registry.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.arachna.bower.registry.BowerPackage;
import org.arachna.bower.registry.BowerRegistry;

/**
 * @author Dirk Weigenand
 */
public class Registry implements BowerRegistry {
    /**
     * the bower registry to use.
     */
    private final BowerRegistry privateBowerRegistry;

    /**
     * Collection of remote bower registries.
     */
    private Collection<BowerRegistry> remoteBowerRegistries = new LinkedList<BowerRegistry>();

    public Registry(BowerRegistry privateBowerRegistry, Collection<BowerRegistry> remoteBowerRegistries) {
        this.privateBowerRegistry = privateBowerRegistry;
        this.remoteBowerRegistries.addAll(remoteBowerRegistries);
    }

    @Override
    public Collection<BowerPackage> getAllPackages() {
        Collection<BowerPackage> packages = new LinkedList<BowerPackage>();

        for (BowerRegistry registry : getAllRegistries()) {
            packages.addAll(registry.getAllPackages());
        }

        return packages;
    }

    @Override
    public void register(BowerPackage bowerPackage) throws IllegalArgumentException {
        this.privateBowerRegistry.register(bowerPackage);
    }

    @Override
    public BowerPackage getPackage(String packageName) {
        BowerPackage bowerPackage = null;

        for (BowerRegistry registry : getAllRegistries()) {
            bowerPackage = registry.getPackage(packageName);

            if (bowerPackage != null) {
                break;
            }
        }

        return bowerPackage;
    }

    @Override
    public Collection<BowerPackage> search(String packageName) {
        Collection<BowerPackage> packages = new LinkedList<BowerPackage>();

        for (BowerRegistry registry : getAllRegistries()) {
            packages.addAll(registry.search(packageName));
        }

        return packages;
    }

    private Collection<BowerRegistry> getAllRegistries() {
        Collection<BowerRegistry> registries = new ArrayList<BowerRegistry>();

        registries.add(privateBowerRegistry);
        registries.addAll(remoteBowerRegistries);

        return registries;
    }
}
