/**
 *
 */
package org.arachna.bower.registry.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.arachna.bower.registry.BowerPackage;
import org.arachna.bower.registry.BowerRegistry;
import org.arachna.bower.registry.impl.RegistryHolder;
import org.arachna.bower.registry.model.BowerPackageDescriptor;

/**
 * @author Dirk Weigenand
 */
@Path("/packages")
public class BowerRegistryRestService {
    /**
     * The bower registry to use.
     */
    private final BowerRegistry registry;

    public BowerRegistryRestService() {
        registry = RegistryHolder.getRegistry();

        if (registry == null) {
            throw new IllegalStateException("The bower registry could not be initialized.");
        }
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

    /**
     * Adds a package to the registry
     *
     * @param name
     *            The name of the package
     * @param url
     *            The repository url
     * @throws URISyntaxException
     */
    @POST
    public Response addPackage(@FormParam("name") String name, @FormParam("url") String url) throws URISyntaxException {
        registry.register(new BowerPackageDescriptor(name, url));

        return Response.created(new URI("/packages/" + name)).build();
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPackage(@PathParam("name") String name) {
        ResponseBuilder builder = Response.ok();
        BowerPackage bowerPackage = registry.getPackage(name);

        if (bowerPackage != null) {
            builder.entity(bowerPackage);
        }
        else {
            builder = Response.status(Status.NOT_FOUND);
        }

        return builder.build();
    }

    @GET
    @Path("/search/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchPackages(@PathParam("name") String name) {
        ResponseBuilder builder = Response.ok();
        
        builder.entity(registry.search(name));
        
        return builder.build();
    }
}
