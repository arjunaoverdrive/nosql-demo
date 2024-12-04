package eu.senla.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.pubsub")
@Data
public class PubSubProperties {

    private String topic;
}
