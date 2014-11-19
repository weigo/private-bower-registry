/**
 * 
 */
package org.arachna.bower.registry.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.arachna.bower.registry.impl.RegistryBuilder;
import org.arachna.bower.registry.impl.RegistryHolder;

/**
 * {@link ServletContextListener} for context startup/shutdown handling.
 * 
 * @author Dirk Weigenand
 */
public class ContextListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        RegistryHolder.setRegistry(new RegistryBuilder().build());
    }
}
