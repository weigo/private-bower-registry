/**
 * 
 */
package org.arachna.bower.registry.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    private Registry() {

        File registryHomeDir = new File(System.getProperty("org.arachna.bower.registry.home", "java.io.tmpdir"));
        File registryBaseDir = new File(registryHomeDir, "/registry");

        createFolderIfNotExistantAndVerify(registryHomeDir);
        createFolderIfNotExistantAndVerify(registryBaseDir);

        this.privateBowerRegistry = readPersistedPackages(registryBaseDir);

        try {
            Properties config = readConfiguration(new FileReader(new File(registryHomeDir, "PrivateBowerRegistry.properties")));
            initializeRemoteRegistries(config);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void initializeRemoteRegistries(Properties config) throws IOException, FileNotFoundException {
        for (String registry : config.getProperty("remote.registries", RemoteBowerRegistry.GLOBAL_BOWER_REGISTRY).split(",")) {
            try {
                this.remoteBowerRegistries.add(new RemoteBowerRegistry(registry));
            }
            catch (IllegalArgumentException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An error occured configuring the remote bower repositories.", e);
            }
        }
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

        return null;
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
