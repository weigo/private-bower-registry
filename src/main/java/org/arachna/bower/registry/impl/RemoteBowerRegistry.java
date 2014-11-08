/**
 *
 */
package org.arachna.bower.registry.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.arachna.bower.registry.BowerPackage;
import org.arachna.bower.registry.BowerRegistry;
import org.arachna.bower.registry.model.BowerPackageDescriptor;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

/**
 * A remote bower registry. This a proxy that supports only a subset of the functionality of {@link BowerRegistry}, namely reading all
 * packages.
 *
 * @author Dirk Weigenand
 */
public class RemoteBowerRegistry implements BowerRegistry {
    /**
     * URL to global bower registry.
     */
    public static final String GLOBAL_BOWER_REGISTRY = "https://bower.herokuapp.com/packages";

    /**
     * REST client to use for querying the remote bower registry.
     */
    private final Client client;

    /**
     * URL to remote bower registry given in the constructor.
     */
    private final String remoteRegistryUrl;

    /**
     * Cache for requests that could be resolved from this remote bower registry.
     */
    private final BowerRegistry cache = new BowerPackageMap();

    /**
     * Create an instance of a remote bower registry using the given URL.
     * 
     * @param remoteRegistryUrl
     *            URL to remote bower registry.
     */
    public RemoteBowerRegistry(String remoteRegistryUrl, String proxyUrl) {
        if (StringUtils.isEmpty(remoteRegistryUrl)) {
            throw new IllegalArgumentException("remote bower repository URL must not be empty!");
        }
        URL url = null;

        try {
            url = new URL(remoteRegistryUrl);

            if (!(url.getProtocol().matches("https?"))) {
                throw new IllegalArgumentException("remote bower repository URL must use http or https as protocol!");
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        this.remoteRegistryUrl = remoteRegistryUrl;
        client = newHttpClient(proxyUrl);
    }

    Client newHttpClient(String proxyAddress) {
        ClientConfig cc = new ClientConfig();

        if (StringUtils.isNotEmpty(proxyAddress)) {
            cc.property(ClientProperties.PROXY_URI, proxyAddress);
            cc.connectorProvider(new ApacheConnectorProvider());
        }

        return ClientBuilder.newClient(cc);
    }

    @Override
    public Collection<BowerPackage> getAllPackages() {
        return executeRequest1("");
    }

    @Override
    public void register(final BowerPackage bowerPackage) throws IllegalArgumentException {
        throw new UnsupportedOperationException(
            "This implementation acts as a read-only proxy and does not support registration of bower packages on remote repositories!");
    }

    @Override
    public BowerPackage getPackage(final String packageName) {
        BowerPackage bowerPackage = cache.getPackage(packageName);

        if (bowerPackage == null) {
            bowerPackage = executeRequest(packageName);
        }

        if (bowerPackage != null) {
            cache.register(bowerPackage);
        }

        return bowerPackage;
    }

    @Override
    public Collection<BowerPackage> search(final String packageName) {
        return executeRequest1("/search/" + packageName);
    }

    private BowerPackage executeRequest(String path) {
        final WebTarget target = client.target(remoteRegistryUrl);

        BowerPackageDescriptor descriptor =
            target.path(path).request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<BowerPackageDescriptor>() {
            });

        return descriptor;
    }

    private Collection<BowerPackage> executeRequest1(String path) {
        final WebTarget target = client.target(remoteRegistryUrl);
        Collection<BowerPackage> packages = new ArrayList<BowerPackage>();
        packages.addAll(target.path(path).request(MediaType.APPLICATION_JSON_TYPE)
            .get(new GenericType<Collection<BowerPackageDescriptor>>() {
            }));

        return packages;
    }
}
