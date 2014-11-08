/**
 *
 */
package org.arachna.bower.registry.rest;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.Response;

import org.arachna.bower.registry.BowerPackage;
import org.arachna.bower.registry.BowerRegistry;
import org.arachna.bower.registry.model.BowerPackageDescriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link BowerRegistryRestService}.
 *
 * @author Dirk Weigenand
 */
public class BowerRegistryRestServiceTest {
    /**
     * Instance under test.
     */
    private BowerRegistryRestService service;

    /**
     * The registry to use.
     */
    private BowerRegistry registry;

    /**
     * Create service instance.
     */
    @Before
    public void setUp() {
        registry = mock(BowerRegistry.class);
        service = new BowerRegistryRestService(registry);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        service = null;
    }

    /**
     * A pristine instance of {@link BowerRegistryRestService} should return an empty list in the entity.
     */
    @Test
    public final void assertGetAllPackagesReturnsEmptyCollectionOfPackages() {
        assertReturnedPackageSize(new ArrayList<BowerPackage>());
    }

    /**
     * An instance of {@link BowerRegistryRestService} with one registered package should return a list with one entry in the entity.
     */
    @Test
    public final void assertGetAllPackagesReturnsCollectionOfPackagesWithOneElement() {
        final ArrayList<BowerPackage> packages = new ArrayList<BowerPackage>();
        packages.add(new BowerPackage() {

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getUrl() {
                // TODO Auto-generated method stub
                return null;
            }
        });

        assertReturnedPackageSize(packages);
    }

    private void assertReturnedPackageSize(final Collection<BowerPackage> packages) {
        when(registry.getAllPackages()).thenReturn(packages);
        final Response response = service.getAllPackages();

        assertThat(response, notNullValue(Response.class));
        final Collection<BowerPackageDescriptor> descriptors = (Collection<BowerPackageDescriptor>)response.getEntity();

        assertThat(descriptors, hasSize(packages.size()));

    }
}
