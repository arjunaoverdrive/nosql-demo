package eu.senla.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.senla.config.properties.PubSubProperties;
import eu.senla.listener.Subscriber;
import eu.senla.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@EnableConfigurationProperties(PubSubProperties.class)
@RequiredArgsConstructor
public class PubSubConfiguration {

    private final LettuceConnectionFactory lettuceConnectionFactory;
    private final PubSubProperties properties;
    private final NotificationService notificationService;

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(properties.getTopic());
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        Subscriber delegate = new Subscriber(notificationService, objectMapper());
        return new MessageListenerAdapter(delegate);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory);
        container.addMessageListener(messageListenerAdapter(), channelTopic());
        return container;
    }
}
