package de.unistuttgart.iste.gits.quiz_service.dapr;

import de.unistuttgart.iste.gits.common.event.UserProgressLogEvent;
import io.dapr.client.DaprClient;
import lombok.extern.slf4j.Slf4j;

/**
 * Component that takes care of publishing messages to a dapr Topic
 */
@Slf4j
public class TopicPublisher {

    private static final String PUBSUB_NAME = "gits";
    private static final String USER_PROGRESS_LOG_TOPIC = "content-progressed";

    private final DaprClient client;

    public TopicPublisher(DaprClient client) {
        this.client = client;
    }


    /**
     * Publishes a message to the dapr topic that a user has worked on a content
     *
     * @param userProgressLogEvent event to publish
     */
    public void notifyUserWorkedOnContent(UserProgressLogEvent userProgressLogEvent) {
        client.publishEvent(PUBSUB_NAME, USER_PROGRESS_LOG_TOPIC, userProgressLogEvent).block();
    }

}
