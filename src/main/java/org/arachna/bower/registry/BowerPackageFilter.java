/**
 *
 */
package org.arachna.bower.registry;


/**
 * Filter interface for filtering instances of {@link BowerPackage}.
 *
 * @author Dirk Weigenand
 */
public interface BowerPackageFilter {
    /**
     * Accept the given {@link BowerPackage} when a certain condition is met.
     *
     * @param bowerPackage
     *            an instance of {@link BowerPackage} to test against a condition.
     * @return <code>true</code>, when the condition implemented by a concrete instance of this interface is met, <code>false</code>
     *         otherwise.
     */
    boolean accept(BowerPackage bowerPackage);
}
