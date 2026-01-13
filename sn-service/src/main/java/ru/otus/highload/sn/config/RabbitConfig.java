package ru.otus.highload.sn.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "postsExchange";
    public static final String QUEUE_NAME_USER = "postUserQueue";
    public static final String QUEUE_NAME_CELEBRITY = "postUserCelebrity";
    public static final String ROUTING_KEY_USER = "user.*";
    public static final String ROUTING_KEY_CELEBRITY = "celebrity.*";

    @Value("${rabbit.celebrity-queue.batchSize}")
    private Integer batchSize;

    @Value("${rabbit.celebrity-queue.batchTimeOut}")
    private Long batchTimeOut;

    @Bean
    public Queue queueUser() {
        return new Queue(QUEUE_NAME_USER, true);
    }

    @Bean
    public Queue queueCelebrity() {
        return new Queue(QUEUE_NAME_CELEBRITY, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding bindingUser(@Qualifier("queueUser") Queue queueUser, TopicExchange exchange) {
        return BindingBuilder.bind(queueUser)
                .to(exchange)
                .with(ROUTING_KEY_USER);
    }

    @Bean
    public Binding bindingCelebrity(@Qualifier("queueCelebrity") Queue queueCelebrity, TopicExchange exchange) {
        return BindingBuilder.bind(queueCelebrity)
                .to(exchange)
                .with(ROUTING_KEY_CELEBRITY);
    }

    @Bean("rabbitBatchListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitBatchListenerContainerFactory(ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConsumerBatchEnabled(true);
        factory.setBatchSize(batchSize);
        factory.setBatchReceiveTimeout(batchTimeOut);
        return factory;
    }

}
