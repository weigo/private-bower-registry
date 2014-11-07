/**
 * 
 */
package org.arachna.bower.registry.impl;

import org.arachna.bower.registry.BowerRegistry;

/**
 * @author Dirk Weigenand
 */
public class RegistryHolder {
    /**
     * 
     */
    private static BowerRegistry REGISTRY;

    /**
     * 
     * @param registry
     */
    public static void setRegistry(BowerRegistry registry) {
        REGISTRY = registry;
    }

    public static BowerRegistry getRegistry() {
        return REGISTRY;
    }
}
