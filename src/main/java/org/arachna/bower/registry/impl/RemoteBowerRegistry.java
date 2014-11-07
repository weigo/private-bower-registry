/**
 *
 */
package org.arachna.bower.registry.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.arachna.bower.registry.BowerPackage;
import org.arachna.bower.registry.BowerRegistry;
import org.arachna.bower.registry.model.BowerPackageDescriptor;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

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
    private final Client client = Client.create();

    /**
     * URL to remote bower registry given in the constructor.
     */
    private final String remoteRegistryUrl;

    /**
     * Create an instance of a remote bower registry using the given URL.
     * 
     * @param remoteRegistryUrl URL to remote bower registry.
     */
    public RemoteBowerRegistry(String remoteRegistryUrl) {
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
    }

    /**
     * Create an instance of a remote bower registry using {@see #GLOBAL_BOWER_REGISTRY}.
     */
    public RemoteBowerRegistry() {
        this(GLOBAL_BOWER_REGISTRY);
    }

    @Override
    public Collection<BowerPackage> getAllPackages() {
        final WebResource resource = client.resource(remoteRegistryUrl);
        final Collection<BowerPackage> packages = new LinkedList<BowerPackage>();

        try {
            final JSONArray remotePackages = new JSONArray(resource.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class));

            for (int i = 0; i < remotePackages.length(); i++) {
                final JSONObject remotePackage = (JSONObject)remotePackages.get(i);
                packages.add(new BowerPackageDescriptor(remotePackage.getString("name"), remotePackage.getString("url")));
            }
        }
        catch (final UniformInterfaceException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                String.format("There was an error fetching the list of all packages from %s.", this.remoteRegistryUrl), e);
        }
        catch (final ClientHandlerException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                String.format("There was an error fetching the list of all packages from %s.", this.remoteRegistryUrl), e);
        }
        catch (final JSONException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                String.format("There was an error transforming the list of packages from %s to JSON.", this.remoteRegistryUrl), e);
        }

        return packages;
    }

    @Override
    public void register(final BowerPackage bowerPackage) throws IllegalArgumentException {
        throw new UnsupportedOperationException(
            "This implementation acts as a read-only proxy and does not support registration of bower packages on remote repositories!");
    }

    @Override
    public BowerPackage getPackage(final String packageName) {
        throw new UnsupportedOperationException(
            "This implementation acts as a read-only proxy and does not support getting of bower packages from remote repositories!");
    }

    @Override
    public Collection<BowerPackage> search(final String packageName) {
        throw new UnsupportedOperationException(
            "This implementation acts as a read-only proxy and does not support searching of bower packages in remote repositories!");
    }
}
