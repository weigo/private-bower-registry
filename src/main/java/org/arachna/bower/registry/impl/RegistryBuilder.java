/**
 * 
 */
package org.arachna.bower.registry.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.bower.registry.BowerRegistry;

/**
 * Builder for configured {@link BowerRegistry} instances.
 * 
 * @author Dirk Weigenand
 */
public class RegistryBuilder {
    /**
     * Build a {@link BowerRegistry} using the configuration.
     * 
     * @return a bower registry using a file backed registry and the remote bower registry from the configuration file.
     */
    public BowerRegistry build() {
        File registryHomeDir = new File(System.getProperty("org.arachna.bower.registry.home", "java.io.tmpdir"));
        File registryBaseDir = new File(registryHomeDir, "/registry");

        createFolderIfNotExistantAndVerify(registryHomeDir);
        createFolderIfNotExistantAndVerify(registryBaseDir);

        Properties config = new Properties();

        try {
            config = readConfiguration(new FileReader(new File(registryHomeDir, "PrivateBowerRegistry.properties")));
        }
        catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", e);
        }

        return new Registry(readPersistedPackages(registryBaseDir), createRemoteRegistries(config));
    }

    /**
     * Read remote registries from the given configuration.
     * 
     * @param config
     *            properties containing the configuration.
     * @return a collection of configured remote bower registries.
     */
    private Collection<BowerRegistry> createRemoteRegistries(Properties config) {
        Collection<BowerRegistry> registries = new LinkedList<BowerRegistry>();

        for (String registry : config.getProperty("remote.registries", RemoteBowerRegistry.GLOBAL_BOWER_REGISTRY).split(",")) {
            try {
                registries.add(new RemoteBowerRegistry(registry));
            }
            catch (IllegalArgumentException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An error occured configuring the remote bower repositories.", e);
            }
        }

        return registries;
    }

    private Properties readConfiguration(Reader config) {
        Properties properties = new Properties();
        try {
            properties.load(config);
        }
        catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An error occured reading the private bower registry configuration.",
                e);
        }

        return properties;
    }

    private void createFolderIfNotExistantAndVerify(File folder) {
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                throw new IllegalStateException(String.format("Could not create folder '%s'", folder.getAbsolutePath()));
            }
        }

        if (!folder.isDirectory()) {
            throw new IllegalStateException("The path '%s' does not a denote directory!");
        }

        if (!folder.canRead() || !folder.canWrite() || !folder.canExecute()) {
            throw new IllegalStateException(
                "Please check your permissions on the folder '%s'! Permissions should be read, write and execute.");
        }
    }

    private BowerRegistry readPersistedPackages(File registryBaseDir) {
        File packages = new File(registryBaseDir, FileBackedBowerRegistry.BOWER_PACKAGES);
        FileBackedBowerRegistry bowerRegistry = new FileBackedBowerRegistry(new BowerPackageMap(), registryBaseDir);

        if (packages.exists()) {
            try {
                bowerRegistry.load(new FileReader(packages));
            }
            catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    String.format("An error occured reading packages from '%s'.", packages.getAbsolutePath()), e);
            }
        }

        return bowerRegistry;
    }
}
