package ru.otus.highload.sn;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "postsExchange";
    public static final String QUEUE_NAME_USER = "postUserQueue";
    public static final String ROUTING_KEY_USER = "user.*";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME_USER, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(ROUTING_KEY_USER);
    }

}
