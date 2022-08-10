package com.faltdor.libraryconsumerservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class LibraryConsumer {
    private static final String LIBRARY_EVENTS_TOPIC = "library-events";

    @KafkaListener( topics = { LIBRARY_EVENTS_TOPIC } )
    public void onMessage( ConsumerRecord<Integer, String> consumerRecord ) {

        log.info( "consumerRecord {}", consumerRecord );
    }
}
