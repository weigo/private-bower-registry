/**
 * 
 */
package org.arachna.bower.registry.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
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
        Configuration config = readOrCreateConfiguration();

        File registryBaseDir = new File(config.getRegistryBase());

        createFolderIfNotExistsAndVerify(registryBaseDir);
        BowerRegistry persistentBowerRegistry = FileBackedBowerRegistry.create(registryBaseDir, new BowerPackageMap());
        return new Registry(persistentBowerRegistry, createRemoteRegistries(config));
    }

    private Configuration readOrCreateConfiguration() {
        File homeDirectory = getHomeDirectory();
        File configurationFile = new File(homeDirectory, ".privateBowerRegistry");

        Configuration config = null;
        FileReader configReader = null;

        try {
            configReader = new FileReader(configurationFile);
            config = new ConfigurationReader(homeDirectory.getAbsolutePath()).read(configReader);
        }
        catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                String.format("Could not read registry configuration file '%s'! Using defaults.", configurationFile.getAbsolutePath()));
            config = new Configuration(new Properties(), homeDirectory.getAbsolutePath());
            writeDefaultConfiguration(configurationFile, config);
        }

        return config;
    }

    private File getHomeDirectory() {
        String home = null;
        List<String> homeEnvironmentVariables = Arrays.asList("HOME", "HOMEPATH", "USERPROFILE");

        for (String env : homeEnvironmentVariables) {
            home = System.getenv(env);

            if (home != null) {
                break;
            }
        }

        if (home == null) {
            throw new IllegalStateException(String.format("Unable to determine home directory from environment variables: %s.",
                homeEnvironmentVariables));
        }
        File homeDirectory = new File(home);
        return homeDirectory;
    }

    private void writeDefaultConfiguration(File configurationFile, Configuration config) {
        Writer configWriter = null;

        try {
            configWriter = new FileWriter(configurationFile);
            new ConfigurationWriter(config).write(configWriter);
        }
        catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                String.format("Could not write default registry configuration file '%s'!", configurationFile.getAbsolutePath()), e);
        }
        finally {
            if (configWriter != null) {
                try {
                    configWriter.close();
                }
                catch (IOException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                        String.format("Could not write default registry configuration file '%s'!", configurationFile.getAbsolutePath()), e);
                }
            }
        }
    }

    /**
     * Writer for private bower repository configuration objects.
     * 
     * @author Dirk Weigenand
     */
    class ConfigurationWriter {
        private final Configuration configuration;

        ConfigurationWriter(Configuration configuration) {
            this.configuration = configuration;
        }

        void write(Writer writer) throws IOException {
            Properties properties = new Properties();
            properties.put(Configuration.ConfigurationProperties.PROXY_ULR.getPropertyName(), configuration.getProxyUrl());
            properties.put(Configuration.ConfigurationProperties.REGISTRY_FOLDER.getPropertyName(), configuration.getRegistryBase());
            properties.put(Configuration.ConfigurationProperties.REMOTE_REGISTRIES.getPropertyName(),
                StringUtils.join(configuration.getRemoteRepositories(), ","));

            properties.store(writer, "private bower registry configuration file");
        }
    }

    /**
     * Read remote registries from the given configuration.
     * 
     * @param config
     *            properties containing the configuration.
     * @return a collection of configured remote bower registries.
     */
    private Collection<BowerRegistry> createRemoteRegistries(Configuration config) {
        Collection<BowerRegistry> registries = new LinkedList<BowerRegistry>();

        for (String registry : config.getRemoteRepositories()) {
            try {
                registries.add(new RemoteBowerRegistry(registry, config.getProxyUrl()));
            }
            catch (IllegalArgumentException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An error occured configuring the remote bower repositories.", e);
            }
        }

        return registries;
    }

    class ConfigurationReader {
        private final String homeDirectory;

        ConfigurationReader(final String homeDirectory) {
            this.homeDirectory = homeDirectory;
        }

        Configuration read(Reader reader) {
            return new Configuration(readConfiguration(reader), homeDirectory);
        }

        private Properties readConfiguration(Reader config) {
            Properties properties = new Properties();

            try {
                properties.load(config);
            }
            catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "An error occured reading the private bower registry configuration.", e);
            }

            return properties;
        }
    }

    private void createFolderIfNotExistsAndVerify(File folder) {
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

    private static class Configuration {
        /**
         * folder where the bower registry should store locally registered bower packages.
         */
        private String registryBase;

        /**
         * list of remote bower packages to query.
         */
        private final Collection<String> remoteRepositories = new LinkedList<String>();

        /**
         * URL of proxy to use when querying remote repositories.
         */
        private final String proxyUrl;

        /**
         * names of properties valid in configuration file.
         * 
         * @author Dirk Weigenand
         */
        enum ConfigurationProperties {
            /**
             * URL to proxy if one should used.
             */
            PROXY_ULR("proxyUrl"),

            /**
             * List of remote bower registries to use.
             */
            REMOTE_REGISTRIES("remote.registries"),

            /**
             * Folder where list of locally registered bower packages should be stored.
             */
            REGISTRY_FOLDER("registry.folder");

            /**
             * name of property in configuration file.
             */
            private final String propertyName;

            private ConfigurationProperties(final String propertyName) {
                this.propertyName = propertyName;
            }

            /**
             * @return the propertyName
             */
            public String getPropertyName() {
                return propertyName;
            }
        }

        /**
         * Create a new instance of a configuration for a private bower registration.
         * 
         * @param properties
         * @param home
         */
        Configuration(Properties properties, String home) {
            proxyUrl = StringUtils.trimToEmpty(properties.getProperty(ConfigurationProperties.PROXY_ULR.getPropertyName()));
            registryBase = StringUtils.trimToEmpty(properties.getProperty(ConfigurationProperties.REGISTRY_FOLDER.getPropertyName()));

            if (StringUtils.isEmpty(registryBase)) {
                registryBase = new File(home, ".bowerRegistry").getAbsolutePath();
            }

            for (String remoteRegistryUrl : properties.getProperty(ConfigurationProperties.REMOTE_REGISTRIES.getPropertyName(),
                RemoteBowerRegistry.GLOBAL_BOWER_REGISTRY).split(",")) {
                if (StringUtils.isNotEmpty(remoteRegistryUrl)) {
                    remoteRepositories.add(StringUtils.trimToEmpty(remoteRegistryUrl));
                }
            }
        }

        /**
         * Get the folder name where the registry shall store its locally registered bower packages.
         * 
         * @return folder name where the registry shall store its locally registered bower packages.
         */
        public String getRegistryBase() {
            return registryBase;
        }

        /**
         * Get URL of proxy to use when querying remote bower packages.
         * 
         * @return URL of proxy to use when querying remote bower packages.
         */
        public String getProxyUrl() {
            return proxyUrl;
        }

        /**
         * Get list of remote bower repositories to query.
         * 
         * @return list of remote bower repositories to query.
         */
        public Collection<String> getRemoteRepositories() {
            return Collections.unmodifiableCollection(remoteRepositories);
        }
    }
}
