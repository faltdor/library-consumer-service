package com.faltdor.libraryconsumerservice.service;

import com.faltdor.libraryconsumerservice.consumer.entity.LibraryEvent;
import com.faltdor.libraryconsumerservice.repository.LibraryEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@AllArgsConstructor
public class LibraryEventService {

    private final LibraryEventRepository libraryEventRepository;

    private final ObjectMapper objectMapper;

    public void processLibraryEvent( ConsumerRecord<Integer, String> consumerRecord ) throws JsonProcessingException {

        final LibraryEvent libraryEvent = objectMapper.readValue( consumerRecord.value(), LibraryEvent.class );

        log.info( "Library Event {}", libraryEvent );


        switch ( libraryEvent.getLibraryEventType() ) {
            case NEW:
                save( libraryEvent );
                break;
            case UPDATE:
                //update();
                break;
            default:
                log.warn( "Can not process messages unknown event type {} ", libraryEvent.getLibraryEventType() );
        }
    }

    private void save( final LibraryEvent libraryEvent ) {

        libraryEvent.getBook().setLibraryEvent( libraryEvent );
        libraryEventRepository.save( libraryEvent );
        log.info( "Successfully Persisted event {}", libraryEvent );
    }
}

