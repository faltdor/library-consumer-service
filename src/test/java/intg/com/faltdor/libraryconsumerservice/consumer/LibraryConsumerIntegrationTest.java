package com.faltdor.libraryconsumerservice.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;


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

    @BeforeEach
    public void setUp() {

        for ( MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers() ) {
            ContainerTestUtils.waitForAssignment( messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic() );
        }
    }
}
