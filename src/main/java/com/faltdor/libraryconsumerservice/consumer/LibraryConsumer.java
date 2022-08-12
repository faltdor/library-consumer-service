package com.faltdor.libraryconsumerservice.consumer;

import com.faltdor.libraryconsumerservice.service.LibraryEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@AllArgsConstructor
public class LibraryConsumer {
    private static final String LIBRARY_EVENTS_TOPIC = "library-events";

    private final LibraryEventService libraryEventService;

    @KafkaListener( topics = { LIBRARY_EVENTS_TOPIC } )
    public void onMessage( ConsumerRecord<Integer, String> consumerRecord ) throws JsonProcessingException {

        log.info( "consumerRecord {}", consumerRecord );
        libraryEventService.processLibraryEvent( consumerRecord );
    }
}
