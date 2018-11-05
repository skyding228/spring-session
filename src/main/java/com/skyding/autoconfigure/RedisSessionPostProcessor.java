package com.skyding.autoconfigure;

import com.skyding.autoconfigure.configuration.RedisSessionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * created at 2018/11/5
 *
 * @author weichunhe
 */
public class RedisSessionPostProcessor implements BeanDefinitionRegistryPostProcessor {
    static Logger LOG = LoggerFactory.getLogger(RedisSessionConfiguration.class);

    private BeanDefinitionRegistry definitionRegistry;

    /**
     * Modify the application context's internal bean definition registry after its
     * standard initialization. All regular bean definitions will have been loaded,
     * but no beans will have been instantiated yet. This allows for adding further
     * bean definitions before the next post-processing phase kicks in.
     *
     * @param registry the bean definition registry used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        definitionRegistry = registry;
    }

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        XmlBeanDefinitionReader definitionReader = new XmlBeanDefinitionReader(definitionRegistry);
        PathMatchingResourcePatternResolver pmrl = new PathMatchingResourcePatternResolver(beanFactory.getBeanClassLoader());
        Resource[] resources = null;
        try {
            resources = pmrl.getResources("classpath*:META-INF/spring-session.xml");
        } catch (IOException e) {
            LOG.error("{} cannot load resources.", RedisSessionContextListener.MODULE_NAME, e);
            throw new IllegalStateException(
                    RedisSessionContextListener.MODULE_NAME + "Cannot initialize spring-session because of " + e.getMessage());
        }
        for (Resource r : resources) {
            int i = definitionReader.loadBeanDefinitions(r);
            LOG.info("{} loaded {} and registered {} beans.", RedisSessionContextListener.MODULE_NAME, r.getFilename(), i);
        }
    }
}
