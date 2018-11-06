package com.skyding.autoconfigure;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;

/**
 * created at 2018/11/6
 *
 * @author weichunhe
 */
public class RedisClusterConfigurationFactory implements FactoryBean<RedisClusterConfiguration>, InitializingBean {
    /**
     * where is the configuration file locate.
     * <p>
     * All the configurable properties are:
     *
     * @see org.springframework.data.redis.connection.RedisClusterConfiguration
     * spring.redis.cluster.nodes
     * <p>
     * spring.redis.cluster.max-redirects
     */
    private String configurationFile;

    private RedisClusterConfiguration clusterConfiguration;

    @Override
    public RedisClusterConfiguration getObject() throws Exception {
        return clusterConfiguration;
    }

    @Override
    public Class<?> getObjectType() {
        return RedisClusterConfiguration.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ResourcePropertySource propertySource = new ResourcePropertySource(configurationFile);
        clusterConfiguration = new RedisClusterConfiguration(propertySource);
    }

    public String getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }
}
