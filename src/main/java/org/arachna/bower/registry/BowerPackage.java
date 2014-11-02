/**
 *
 */
package org.arachna.bower.registry;

/**
 * A bower package. Bower packages have
 * <ul>
 * <li>a package name</li>
 * <li>and can be obtained from a (GitHub-) URL.</li>
 * </ul>
 *
 * @author Dirk Weigenand
 */
public interface BowerPackage {
    /**
     * Return the name of the bower package.
     *
     * @return name of the bower package.
     */
    String getName();

    /**
     * The URL of repository as String.
     *
     * @return The URL of repository as String.
     */
    String getURL();
}
