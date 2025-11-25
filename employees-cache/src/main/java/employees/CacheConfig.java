package employees;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.ObjectMapper;

//Gyorsabb indítás miatt proxyBeanMethods = false
@Configuration(proxyBeanMethods = false)
public class CacheConfig {
    @Bean
    public ReactiveRedisTemplate<Long, Employee> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        RedisSerializer<Employee> redisSerializer = new JacksonJsonRedisSerializer<>(objectMapper, Employee.class);
        RedisSerializationContext<Long, Employee> context = RedisSerializationContext
                .<Long, Employee>newSerializationContext(RedisSerializer.string())
                .key(new GenericToStringSerializer<>(Long.class))
                .hashKey(new JacksonJsonRedisSerializer<>(Long.class))
                .value(redisSerializer)
                .hashValue(redisSerializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

}
