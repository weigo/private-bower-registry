/**
 *
 */
package org.arachna.bower.registry.impl;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import org.arachna.bower.registry.BowerPackage;
import org.arachna.bower.registry.BowerRegistry;
import org.arachna.bower.registry.model.BowerPackageDescriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link BowerPackageMap}.
 *
 * @author Dirk Weigenand
 */
public class BowerPackageMapTest {
    /**
     * Instance under test.
     */
    private BowerRegistry registry;

    /**
     * Rule for expecting exceptions.
     */
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        registry = new BowerPackageMap();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        registry = null;
    }

    /**
     * Test method for {@link org.arachna.bower.registry.impl.BowerPackageMap#getAllPackages()}.
     */
    @Test
    public final void testGetAllPackagesReturnsEmptyCollectionOnPristineRegistry() {
        assertThat(registry.getAllPackages(), hasSize(0));
    }

    /**
     * Test method for {@link org.arachna.bower.registry.impl.BowerPackageMap#register(BowerPackage)}.
     */
    @Test
    public final void testGetAllPackagesReturnsCollectionWithOneBowerPackageAfterAddingOneToPristineRegistry() {
        registry.register(new BowerPackageDescriptor("package", "url"));
        assertThat(registry.getAllPackages(), hasSize(1));
    }

    /**
     * Test method for {@link org.arachna.bower.registry.impl.BowerPackageMap#register(BowerPackage)}.
     */
    @Test
    public final void testRegisterABowerPackageTwiceThrowsAnIllegalArgumentException() {
        final BowerPackageDescriptor bowerPackage = new BowerPackageDescriptor("package", "url");
        registry.register(bowerPackage);

        expectedException.expect(IllegalArgumentException.class);
        registry.register(bowerPackage);
    }

    /**
     * Test method for {@link org.arachna.bower.registry.impl.BowerPackageMap#register(BowerPackage)}.
     */
    @Test
    public final void testRegisterRegistersABowerPackageWithTheRegistry() {
        final BowerPackage bowerPackage = new BowerPackageDescriptor("package", "url");
        registry.register(bowerPackage);
        final BowerPackage lookedUpPackage = registry.getPackage(bowerPackage.getName());
        assertThat(lookedUpPackage, notNullValue(BowerPackage.class));
    }

    @Test
    public void testSearchPackageWithNullArgument() {
        registry.register(new BowerPackageDescriptor("package0", "url0"));
        registry.register(new BowerPackageDescriptor("package1", "url1"));

        assertThat(registry.search(null), hasSize(2));
    }

    @Test
    public void testSearchPackageWithEmptyArgument() {
        registry.register(new BowerPackageDescriptor("package0", "url0"));
        registry.register(new BowerPackageDescriptor("package1", "url1"));

        assertThat(registry.search(""), hasSize(2));
    }

    @Test
    public void testSearchPackageWithPrefixArgument() {
        registry.register(new BowerPackageDescriptor("package0", "url0"));
        registry.register(new BowerPackageDescriptor("package1", "url1"));

        assertThat(registry.search("package"), hasSize(2));
    }

}
