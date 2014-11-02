/**
 *
 */
package org.arachna.bower.registry.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.arachna.bower.registry.BowerRegistry;

/**
 * @author Dirk Weigenand
 */
@Path("/packages")
public class BowerRegistryRestService {
    /**
     * The bower registry to use.
     */
    @Inject
    private BowerRegistry registry;

    public BowerRegistryRestService() {

    }

    /**
     *
     * @param registry
     */
    public BowerRegistryRestService(final BowerRegistry registry) {
        this.registry = registry;
    }

    /**
     * Get JSON representation of all registered bower packages.
     *
     * @return a {@link Response} object containing a list of all registered bower packages.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Response getAllPackages() {
        final ResponseBuilder builder = Response.ok();
        builder.entity(registry.getAllPackages());

        return builder.build();
    }
}
