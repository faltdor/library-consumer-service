package com.faltdor.libraryconsumerservice.consumer;

import com.faltdor.libraryconsumerservice.consumer.entity.Book;
import com.faltdor.libraryconsumerservice.repository.BookEventRepository;
import com.faltdor.libraryconsumerservice.service.LibraryEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;


@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@EmbeddedKafka( topics = { "library-events" }, partitions = 3 )
@TestPropertySource( properties = { "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                                    "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                                    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.IntegerSerializer",
                                    "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer",
} )
public class LibraryConsumerIntegrationTest {


    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @SpyBean
    private LibraryConsumer libraryConsumerSpy;

    @SpyBean
    private LibraryEventService libraryEventService;

    @Autowired
    private BookEventRepository bookEventRepository;

    @BeforeEach
    public void setUp() {

        for ( MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers() ) {
            ContainerTestUtils.waitForAssignment( messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic() );
        }
    }

    @AfterEach
    public void tearDown() {

        bookEventRepository.deleteAll();
    }


    @Test
    public void publishAndConsumeLibraryMessage() throws ExecutionException, InterruptedException, JsonProcessingException {

        final String bookName = "name of book";
        final String bookAuthor = "any book";
        final String message = "{\"libraryEvent\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":1,\"bookName\":\"" + bookName + "\",\"bookAuthor\":\"" + bookAuthor + "\"}}";

        kafkaTemplate.sendDefault( message ).get();

        CountDownLatch countDownLatch = new CountDownLatch( 1 );
        countDownLatch.await( 3, TimeUnit.SECONDS );


        verify( libraryConsumerSpy ).onMessage( isA( ConsumerRecord.class ) );
        verify( libraryEventService ).processLibraryEvent( isA( ConsumerRecord.class ) );

        final List<Book> books = (List<Book>) bookEventRepository.findAll();
        assert books.size() == 1;

        final Book book = books.get( 0 );
        assert book.getBookId() == 1;
        assert book.getBookName().equals( bookName );
        assert book.getBookAuthor().equals( bookAuthor );
    }
}
