/**
 * 
 */
package org.arachna.bower.registry.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.bower.registry.BowerPackage;
import org.arachna.bower.registry.BowerRegistry;
import org.arachna.bower.registry.model.BowerPackageDescriptor;

/**
 * A bower registry that persists the registry to a file on calls to {@see #register(BowerPackage)}.
 * 
 * @author Dirk Weigenand
 */
public class FileBackedBowerRegistry implements BowerRegistry {
    /**
     * file name where package definitions should be stored.
     */
    public static final String BOWER_PACKAGES = "bowerPackages.properties";

    /**
     * registry to delegate to.
     */
    private final BowerRegistry delegate;

    /**
     * Folder to use as store for registered packages.
     */
    private final File baseDir;

    /**
     * Create an instance of a {@link BowerRegistry} that persists its registered {@link BowerPackages} to a file in the given directory.
     * The registry parameter is used to delegate all operations to.
     * 
     * @param delegate
     *            bower registry to delegate to.
     * @param baseDir
     *            folder to use when persisting registered packages.
     */
    public FileBackedBowerRegistry(BowerRegistry delegate, File baseDir) {
        this.delegate = delegate;
        this.baseDir = baseDir;
    }

    @Override
    public Collection<BowerPackage> getAllPackages() {
        return delegate.getAllPackages();
    }

    @Override
    public void register(BowerPackage bowerPackage) throws IllegalArgumentException {
        delegate.register(bowerPackage);

        savePackagesToFile();
    }

    private void savePackagesToFile() {
        File oldPackageStore = new File(baseDir, BOWER_PACKAGES);

        try {
            File newPackageStore = File.createTempFile(BOWER_PACKAGES, "", baseDir);
            Writer storeWriter = new FileWriter(newPackageStore);

            for (BowerPackage bowerPackage : delegate.getAllPackages()) {
                storeWriter.append(bowerPackage.getName()).append("=").append(bowerPackage.getUrl()).append("\n");
            }

            storeWriter.close();

            if (oldPackageStore.exists()) {
                oldPackageStore.delete();
            }

            newPackageStore.renameTo(oldPackageStore);
        }
        catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                String.format("An error occured persisting registered bower packages to '%s'.", oldPackageStore.getAbsolutePath()), e);
        }
    }

    @Override
    public BowerPackage getPackage(String packageName) {
        return delegate.getPackage(packageName);
    }

    @Override
    public Collection<BowerPackage> search(String packageName) {
        return delegate.search(packageName);
    }

    /**
     * Load package descriptors from properties given in the <code>packages</code> argument.
     * 
     * @param packages
     *            reader containing package definitions in pairs: <code>package name=package url</code>.
     * @throws IOException
     *             when reading packages from the reader fails.
     */
    public void load(Reader packages) throws IOException {
        Properties properties = new Properties();
        properties.load(packages);

        for (Entry<Object, Object> entry : properties.entrySet()) {
            BowerPackage bowerPackage = new BowerPackageDescriptor((String)entry.getKey(), (String)entry.getValue());
            this.delegate.register(bowerPackage);
        }

        packages.close();
    }
}
