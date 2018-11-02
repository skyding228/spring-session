package com.skyding.autoconfigure;

import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.Iterator;

/**
 * listen to context events,in order to configure spring session.
 * <p>
 * created at 2018/11/2
 *
 * @author weichunhe
 */
public class SessionContextListener implements ServletContextListener {
    /**
     * the filter must be called this name.
     * because {@link org.springframework.web.filter.DelegatingFilterProxy} will retrieve a bean has the same name,
     * which is registered by <code>SpringHttpSessionConfiguration.springSessionRepositoryFilter</code> method.
     */
    final String SESSION_FILTER_NAME = "springSessionRepositoryFilter";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        Object contextAttribute = servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (contextAttribute == null) {
            throw new IllegalStateException(
                    "[spring-session]Cannot initialize spring-session because there is no a root application context present - " +
                            "check whether you have a ContextLoader* definitions in your web.xml!");
        }
        WebApplicationContext applicationContext = (WebApplicationContext) contextAttribute;
        addSessionFilter(servletContext);
    }

    /**
     * add a filter for session to servletContext
     *
     * @param servletContext
     */
    protected void addSessionFilter(ServletContext servletContext) {
        FilterRegistration filterRegistration = servletContext.addFilter(SESSION_FILTER_NAME, DelegatingFilterProxy.class);
        filterRegistration.addMappingForUrlPatterns(null,false,"/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
