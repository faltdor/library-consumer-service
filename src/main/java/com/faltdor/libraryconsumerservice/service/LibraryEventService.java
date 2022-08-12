package com.faltdor.libraryconsumerservice.service;

import com.faltdor.libraryconsumerservice.consumer.entity.LibraryEvent;
import com.faltdor.libraryconsumerservice.repository.BookEventRepository;
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

    private final BookEventRepository bookEventRepository;

    private final ObjectMapper objectMapper;

    public void processLibraryEvent( ConsumerRecord<Integer, String> consumerRecord ) throws JsonProcessingException {

        final LibraryEvent libraryEvent = objectMapper.readValue( consumerRecord.value(), LibraryEvent.class );

        log.info( "Library Event {}", libraryEvent );


        switch ( libraryEvent.getLibraryEventType() ) {
            case NEW:
                save( libraryEvent );
                break;
            case UPDATE:
                validate( libraryEvent );
                save( libraryEvent );
                break;
            default:
                log.warn( "Can not process messages unknown event type {} ", libraryEvent.getLibraryEventType() );
        }
    }

    private void validate( final LibraryEvent libraryEvent ) {

        if ( libraryEvent.getLibraryEventId() != null ) {
            throw new IllegalArgumentException( "Library event id can not be null" );
        }
        libraryEventRepository.findById( libraryEvent.getLibraryEventId() )
                              .orElseThrow( () -> new IllegalArgumentException( "Library event id can not be null" ) );
    }

    private void save( final LibraryEvent libraryEvent ) {

        //libraryEventRepository.save( libraryEvent );
        //libraryEvent.getBook().setLibraryEvent( libraryEvent );
        bookEventRepository.save( libraryEvent.getBook() );
        log.info( "Successfully Persisted event {}", libraryEvent );
    }
}

