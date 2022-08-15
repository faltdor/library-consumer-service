package com.faltdor.libraryconsumerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;


@AutoConfiguration
@EnableKafka
@Slf4j
public class ConsumerConfig {

    public DefaultErrorHandler errorHandler() {

        var defaultErrorHandler = new DefaultErrorHandler( new FixedBackOff( 1000L, 2 ) );

        defaultErrorHandler.addNotRetryableExceptions( IllegalArgumentException.class );


        defaultErrorHandler.setRetryListeners( ( ( record, ex, deliveryAttempt ) ->
                                                       log.warn( "Failed Record in Retry Listener, Exception: {} , deliveryAttempt: {} ", ex.getMessage(), deliveryAttempt ) ) );

        return defaultErrorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> concurrentKafkaListenerContainerFactory( ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
                                                                                                  ConsumerFactory consumerFactory ) {

        final ConcurrentKafkaListenerContainerFactory concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory();
        configurer.configure( concurrentKafkaListenerContainerFactory, consumerFactory );
        concurrentKafkaListenerContainerFactory.setConcurrency( 3 );
        concurrentKafkaListenerContainerFactory.setCommonErrorHandler( errorHandler() );
        return concurrentKafkaListenerContainerFactory;
    }
}
