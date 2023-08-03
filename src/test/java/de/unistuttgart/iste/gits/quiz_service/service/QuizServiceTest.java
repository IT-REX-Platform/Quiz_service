package de.unistuttgart.iste.gits.quiz_service.service;

import de.unistuttgart.iste.gits.common.event.ContentChangeEvent;
import de.unistuttgart.iste.gits.common.event.CrudOperation;
import de.unistuttgart.iste.gits.common.event.UserProgressLogEvent;
import de.unistuttgart.iste.gits.generated.dto.QuestionPoolingMode;
import de.unistuttgart.iste.gits.generated.dto.QuestionType;
import de.unistuttgart.iste.gits.generated.dto.QuizCompletedInput;
import de.unistuttgart.iste.gits.quiz_service.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.MultipleChoiceAnswerEmbeddable;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.MultipleChoiceQuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.mapper.QuizMapper;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import de.unistuttgart.iste.gits.quiz_service.validation.QuizValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class QuizServiceTest {

    private final QuizRepository quizRepository = Mockito.mock(QuizRepository.class);

    private final QuizMapper quizMapper = new QuizMapper(new ModelMapper());
    private final QuizValidator quizValidator = new QuizValidator();
    private final TopicPublisher topicPublisher = Mockito.mock(TopicPublisher.class);
    private final QuizService quizService = new QuizService(quizRepository, quizMapper, quizValidator, topicPublisher);

    @Test
    void removeContentIdsTest() {
        //init
        UUID assessmentId = UUID.randomUUID();

        QuizEntity quizEntity = QuizEntity.builder()
                .assessmentId(assessmentId)
                .questionPool(new ArrayList<>())
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .requiredCorrectAnswers(0)
                .numberOfRandomlySelectedQuestions(0)
                .build();

        ContentChangeEvent contentChangeEvent = ContentChangeEvent.builder()
                .contentIds(List.of(assessmentId))
                .operation(CrudOperation.DELETE)
                .build();

        //mock repository
        when(quizRepository.findAllById(contentChangeEvent.getContentIds())).thenReturn(List.of(quizEntity));

        // invoke method under test
        quizService.removeContentIds(contentChangeEvent);

        verify(quizRepository, times(1)).findAllById(any());
        verify(quizRepository, times(1)).deleteAllInBatch(any());
    }

    @Test
    void removeContentIdsWithNoIdsToBeRemovedTest() {
        //init
        UUID assessmentId = UUID.randomUUID();

        ContentChangeEvent contentChangeEvent = ContentChangeEvent.builder()
                .contentIds(List.of(assessmentId))
                .operation(CrudOperation.DELETE)
                .build();

        //mock repository
        when(quizRepository.findAllById(contentChangeEvent.getContentIds())).thenReturn(new ArrayList<QuizEntity>());

        // invoke method under test
        quizService.removeContentIds(contentChangeEvent);

        verify(quizRepository, times(1)).findAllById(any());
        verify(quizRepository, times(1)).deleteAllInBatch(any());
    }

    @Test
    void removeContentIdsInvalidInputTest() {
        //init
        UUID assessmentId = UUID.randomUUID();

        ContentChangeEvent emptyListDto = ContentChangeEvent.builder()
                .contentIds(List.of())
                .operation(CrudOperation.DELETE)
                .build();

        ContentChangeEvent nullListDto = ContentChangeEvent.builder()
                .contentIds(null)
                .operation(CrudOperation.DELETE)
                .build();

        ContentChangeEvent nullOperationDto = ContentChangeEvent.builder()
                .contentIds(List.of(assessmentId))
                .operation(null)
                .build();

        ContentChangeEvent creationEvent = ContentChangeEvent.builder()
                .contentIds(List.of(assessmentId))
                .operation(CrudOperation.CREATE)
                .build();

        ContentChangeEvent updateEvent = ContentChangeEvent.builder()
                .contentIds(List.of(assessmentId))
                .operation(CrudOperation.UPDATE)
                .build();

        List<ContentChangeEvent> events = List.of(emptyListDto, nullListDto, nullOperationDto, creationEvent, updateEvent);

        for (ContentChangeEvent event : events) {
            //invoke method under test
            quizService.removeContentIds(event);
            verify(quizRepository, never()).findAllById(any());
            verify(quizRepository, never()).deleteAllInBatch(any());
        }

    }

    @Test
    void publishProgressRandomModeTest() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        QuizCompletedInput quizCompletedInput = QuizCompletedInput.builder()
                .setQuizId(assessmentId)
                .setNumberOfCorrectAnswers(2)
                .setNumberOfHintsUsed(0)
                .build();

        QuizEntity quizEntity = QuizEntity.builder().assessmentId(assessmentId)
                .questionPool(new ArrayList<>())
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .requiredCorrectAnswers(1)
                .numberOfRandomlySelectedQuestions(2).build();

        UserProgressLogEvent expectedUserProgressLogEvent = UserProgressLogEvent.builder()
                .userId(userId)
                .contentId(assessmentId)
                .hintsUsed(quizCompletedInput.getNumberOfHintsUsed())
                .success(true)
                .timeToComplete(null)
                .correctness((double) quizCompletedInput.getNumberOfCorrectAnswers() / quizEntity.getNumberOfRandomlySelectedQuestions())
                .build();

        //mock repository
        when(quizRepository.getReferenceById(assessmentId)).thenReturn(quizEntity);
        doNothing().when(topicPublisher).notifyUserWorkedOnContent(any());

        // invoke method under test
        quizService.publishProgress(quizCompletedInput, userId);

        verify(quizRepository, times(1)).getReferenceById(assessmentId);
        verify(topicPublisher, times(1)).notifyUserWorkedOnContent(expectedUserProgressLogEvent);

    }

    @Test
    void publishProgressOrderedModeTest() {
        //init
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        QuizCompletedInput quizCompletedInput = QuizCompletedInput.builder()
                .setQuizId(assessmentId)
                .setNumberOfCorrectAnswers(1)
                .setNumberOfHintsUsed(0)
                .build();

        MultipleChoiceAnswerEmbeddable wrongAnswer = MultipleChoiceAnswerEmbeddable.builder().text("Pick me! Pick Me!").correct(false).feedback("Fell for it").build();
        MultipleChoiceAnswerEmbeddable correctAnswer = MultipleChoiceAnswerEmbeddable.builder().text("No me!").correct(true).feedback("Well done!").build();
        MultipleChoiceQuestionEntity questionEntity = MultipleChoiceQuestionEntity.builder().id(UUID.randomUUID()).number(0).type(QuestionType.MULTIPLE_CHOICE).text("This is a question").answers(List.of(wrongAnswer, correctAnswer)).hint("Wink Wink").build();
        MultipleChoiceQuestionEntity questionEntity2 = MultipleChoiceQuestionEntity.builder().id(UUID.randomUUID()).number(0).type(QuestionType.MULTIPLE_CHOICE).text("This is a question").answers(List.of(wrongAnswer, correctAnswer)).hint("Wink Wink").build();

        QuizEntity quizEntity = QuizEntity.builder().assessmentId(assessmentId)
                .questionPool(List.of(questionEntity, questionEntity2))
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .requiredCorrectAnswers(2)
                .numberOfRandomlySelectedQuestions(2).build();

        UserProgressLogEvent expectedUserProgressLogEvent = UserProgressLogEvent.builder()
                .userId(userId)
                .contentId(assessmentId)
                .hintsUsed(quizCompletedInput.getNumberOfHintsUsed())
                .success(false)
                .timeToComplete(null)
                .correctness((double) quizCompletedInput.getNumberOfCorrectAnswers() / quizEntity.getQuestionPool().size())
                .build();

        //mock repository
        when(quizRepository.getReferenceById(assessmentId)).thenReturn(quizEntity);
        doNothing().when(topicPublisher).notifyUserWorkedOnContent(any());

        // invoke method under test
        quizService.publishProgress(quizCompletedInput, userId);

        verify(quizRepository, times(1)).getReferenceById(assessmentId);
        verify(topicPublisher, times(1)).notifyUserWorkedOnContent(expectedUserProgressLogEvent);

    }

}