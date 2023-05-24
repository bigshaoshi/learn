package com.learn.learnredis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * Redis configuration
 *
 * @author shijie wang
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig extends CachingConfigurerSupport {

    /**
     *  设置 redis 数据默认过期时间，默认1天
     *  设置@cacheable 序列化方式
     * @return
     */
//    @Bean
//    public RedisCacheConfiguration redisCacheConfiguration(){
////        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
//        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
//        //设置key的时效性，一般为一天就过期
//        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())).entryTtl(Duration.ofDays(1));
//        //最后返回我们的配置类
//        return configuration;
//    }

    //忽略所有类型的警告
    @SuppressWarnings("all")
    //spring管理的一个bean，使用@Autowired注入到使用的类即可
    @Bean(name = "redisTemplate")
    //注册相同类型的bean，就不会成功，保持当前只有一个名为"redisTemplate"的bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 自定义缓存key生成策略
     * 使用方法 @Cacheable(keyGenerator="keyGenerator")
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder stringBuilder = new StringBuilder();
            //加入类名
            stringBuilder.append(target.getClass().getName());
            //加入方法名
            stringBuilder.append(method.getName());
            //加入包名
            stringBuilder.append(target.getClass().getPackage());
            //加入查询参数
            for (Object obj : params) {
                ObjectMapper mapper = new ObjectMapper();
                String str = "";
                try {
                    str = mapper.writeValueAsString(obj);
                } catch (JsonProcessingException e) {
                    log.info("jackson 解析 object {0}",obj.toString());
                }
                stringBuilder.append(str.hashCode());
            }
            //最终我们生成的key就是这四种的结合，这就能保证我们生成key的唯一性
            return stringBuilder.toString();
        };
    }

}
