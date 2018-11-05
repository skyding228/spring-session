package com.skyding.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * listen to context events,in order to configure spring session.
 * <p>
 * created at 2018/11/2
 *
 * @author weichunhe
 */
public class RedisSessionContextListener implements ServletContextListener {

    private static Logger LOG = LoggerFactory.getLogger(RedisSessionContextListener.class);
    /**
     * module name which can be used as a log prefix.
     */
    public static final String MODULE_NAME = "[spring-session]";
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
                    MODULE_NAME + "Cannot initialize spring-session because there is no a root application context present - " +
                            "check whether you have a ContextLoader* definitions in your web.xml!");
        }
        XmlWebApplicationContext applicationContext = (XmlWebApplicationContext) contextAttribute;
        if (isEnable(applicationContext)) {
            applicationContext.addBeanFactoryPostProcessor(new RedisSessionPostProcessor());
            applicationContext.refresh();
            addSessionFilter(servletContext);
        }
        LOG.info("{} configured spring-session with Redis automatically.", MODULE_NAME);
    }

    protected RedisConnectionFactory getConnectFactory(ApplicationContext applicationContext) {
        RedisConnectionFactory connectionFactory = applicationContext.getBean(RedisConnectionFactory.class);
        if (connectionFactory == null) {
            throw new IllegalStateException(
                    MODULE_NAME + "Cannot initialize spring-session because there is no a RedisConnectionFactory present. ");
        }
        return connectionFactory;
    }

    /**
     * determine if need to configure spring session automatically. if there is a RedisConnectionFactory bean present, it will configure automatically, otherwise will NOT.
     *
     * @param applicationContext container
     * @return
     */
    public boolean isEnable(ApplicationContext applicationContext) {
        return applicationContext.getBeanNamesForType(RedisConnectionFactory.class).length > 0;
    }

    /**
     * add a filter for session to servletContext
     *
     * @param servletContext
     */
    protected void addSessionFilter(ServletContext servletContext) {
        FilterRegistration filterRegistration = servletContext.addFilter(SESSION_FILTER_NAME, DelegatingFilterProxy.class);
        if (filterRegistration == null) {
            throw new IllegalStateException(
                    MODULE_NAME + "Cannot initialize spring-session because there already is a filter named" + SESSION_FILTER_NAME + ".");
        }
        filterRegistration.addMappingForUrlPatterns(null, false, "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
