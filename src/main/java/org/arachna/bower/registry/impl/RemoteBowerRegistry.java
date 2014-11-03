/**
 *
 */
package org.arachna.bower.registry.impl;

import java.util.Collection;
import java.util.LinkedList;

import javax.ws.rs.core.MediaType;

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
     *
     */
    // FIXME: this should support setting the registry to query in the constructor.
    private static final String GLOBAL_BOWER_REGISTRY = "https://bower.herokuapp.com/packages";
    private final Client client = Client.create();

    @Override
    public Collection<BowerPackage> getAllPackages() {
        final WebResource resource = client.resource(GLOBAL_BOWER_REGISTRY);
        final Collection<BowerPackage> packages = new LinkedList<BowerPackage>();

        try {
            final JSONArray remotePackages = new JSONArray(resource.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class));

            for (int i = 0; i < remotePackages.length(); i++) {
                final JSONObject remotePackage = (JSONObject)remotePackages.get(i);
                packages.add(new BowerPackageDescriptor(remotePackage.getString("name"), remotePackage.getString("url")));
            }
        }
        catch (final UniformInterfaceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final ClientHandlerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
