/**
 *
 */
package org.arachna.bower.registry;

import java.util.Collection;

/**
 * Interface for a bower registry.
 *
 * @author Dirk Weigenand
 */
public interface BowerRegistry {
    /**
     * List all packages registered with this bower registry instance.
     *
     * @return a collection of all packages registered with this bower registry instance.
     */
    Collection<BowerPackage> getAllPackages();

    /**
     * Register a bower package with this repository.
     *
     * @param bowerPackage
     *            the package to register with this repository.
     * @throws IllegalArgumentException
     *             when the given package tried to register repeatedly.
     */
    void register(BowerPackage bowerPackage) throws IllegalArgumentException;

    /**
     * Looks up the package by name.
     *
     * @param packageName
     *            name of package to look up.
     * @return a {@link BowerPackage} matching the given package name, <code>null</code> when none could be found.
     */
    BowerPackage getPackage(String packageName);

    /**
     * Search registry for bower packages by name.
     *
     * @param packageName
     *            name of bower package to search for. Can be empty or <code>null</code>.
     * @return a list of all registered packages when <code>packageName</code> is <code>null</code> or empty, a list of packages matching
     *         the regular expression <code>.*?packageName.*?</code> otherwise.
     */
    Collection<BowerPackage> search(String packageName);
}
