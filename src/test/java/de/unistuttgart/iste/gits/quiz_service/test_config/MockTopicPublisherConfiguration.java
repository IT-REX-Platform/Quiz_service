package de.unistuttgart.iste.gits.quiz_service.test_config;

import de.unistuttgart.iste.gits.quiz_service.dapr.TopicPublisher;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@TestConfiguration
public class MockTopicPublisherConfiguration {

    @Primary
    @Bean
    public TopicPublisher getTestTopicPublisher() {
        final TopicPublisher mockPublisher = Mockito.mock(TopicPublisher.class);
        doNothing().when(mockPublisher).notifyUserWorkedOnContent(any());
        return mockPublisher;
    }
}