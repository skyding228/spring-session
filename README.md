# spring-session
A pluggable module used for configuring spring session with redis,which makes the configuration easier.

# using steps

## 1 add dependency to pom.xml 
```xml
<dependency>
    <groupId>com.skyding.autoconfigure</groupId>
    <artifactId>spring-session</artifactId>
    <version>1.0</version>
</dependency>
```

## 2 register a `org.springframework.data.redis.connection.jedis.JedisConnectionFactory`
`JedisConnectionFactory` must be in RootWebApplicationContext,which means your `web.xml` must includes
```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value>
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```
and register `JedisConnectionFactory` in `applicationContext.xml`;

Belows are some simple examples.
- one node
```xml
<bean id="jedisConnFactory " class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory ">
    <property name="hostName" value="localhost"></property>
    <property name="port" value="6379"></property>
</bean>
```
- cluster
```xml
<bean id="jedisConnFactory " class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory ">
    <constructor-arg name="clusterConfig">
        <bean class="com.skyding.autoconfigure.RedisClusterConfigurationFactory">
            <property name="configurationFile" value="classpath:spring-session-redis.properties"></property>
        </bean>
    </constructor-arg>
</bean>
```
spring-session-redis.properties
```properties
spring.redis.cluster.nodes=127.0.0.1:6379,127.0.0.1:6380
spring.redis.cluster.max-redirects=6
```