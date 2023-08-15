package de.unistuttgart.iste.gits.quiz_service.controller;

import de.unistuttgart.iste.gits.common.event.ContentChangeEvent;
import de.unistuttgart.iste.gits.quiz_service.service.QuizService;
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * REST Controller Class listening to a dapr Topic.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final QuizService quizService;

    @Topic(name = "content-changes", pubsubName = "gits")
    @PostMapping(path = "/quiz-service/content-changes-pubsub")
    public Mono<Void> updateAssociation(@RequestBody CloudEvent<ContentChangeEvent> cloudEvent, @RequestHeader Map<String, String> headers) {

        return Mono.fromRunnable(() -> quizService.removeContentIds(cloudEvent.getData()));
    }

}
