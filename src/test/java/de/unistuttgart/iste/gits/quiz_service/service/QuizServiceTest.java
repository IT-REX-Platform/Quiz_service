package de.unistuttgart.iste.gits.quiz_service.service;

import de.unistuttgart.iste.gits.common.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.common.event.*;
import de.unistuttgart.iste.gits.common.exception.IncompleteEventMessageException;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.mapper.QuizMapper;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import de.unistuttgart.iste.gits.quiz_service.validation.QuizValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        final UUID assessmentId = UUID.randomUUID();

        final QuizEntity quizEntity = QuizEntity.builder()
                .assessmentId(assessmentId)
                .questionPool(new ArrayList<>())
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .requiredCorrectAnswers(0)
                .numberOfRandomlySelectedQuestions(0)
                .build();

        final ContentChangeEvent contentChangeEvent = ContentChangeEvent.builder()
                .contentIds(List.of(assessmentId))
                .operation(CrudOperation.DELETE)
                .build();

        //mock repository
        when(quizRepository.findAllById(contentChangeEvent.getContentIds())).thenReturn(List.of(quizEntity));

        // invoke method under test
        assertDoesNotThrow(() -> quizService.deleteQuizzesWhenQuizContentIsDeleted(contentChangeEvent));

        verify(quizRepository, times(1)).deleteAllByIdInBatch(any());
    }

    @Test
    void removeContentIdsWithNoIdsToBeRemovedTest() {
        //init
        final UUID assessmentId = UUID.randomUUID();

        final ContentChangeEvent contentChangeEvent = ContentChangeEvent.builder()
                .contentIds(List.of(assessmentId))
                .operation(CrudOperation.DELETE)
                .build();

        //mock repository
        when(quizRepository.findAllById(contentChangeEvent.getContentIds())).thenReturn(new ArrayList<>());

        // invoke method under test
        assertDoesNotThrow(() -> quizService.deleteQuizzesWhenQuizContentIsDeleted(contentChangeEvent));

        verify(quizRepository, times(1)).deleteAllByIdInBatch(any());
    }

    @Test
    void removeContentIdsInvalidInputTest() {
        //init
        final UUID assessmentId = UUID.randomUUID();

        final ContentChangeEvent emptyListDto = ContentChangeEvent.builder()
                .contentIds(List.of())
                .operation(CrudOperation.DELETE)
                .build();

        final ContentChangeEvent nullListDto = ContentChangeEvent.builder()
                .contentIds(null)
                .operation(CrudOperation.DELETE)
                .build();

        final ContentChangeEvent nullOperationDto = ContentChangeEvent.builder()
                .contentIds(List.of(assessmentId))
                .operation(null)
                .build();

        final ContentChangeEvent creationEvent = ContentChangeEvent.builder()
                .contentIds(List.of(assessmentId))
                .operation(CrudOperation.CREATE)
                .build();

        final ContentChangeEvent updateEvent = ContentChangeEvent.builder()
                .contentIds(List.of(assessmentId))
                .operation(CrudOperation.UPDATE)
                .build();

        final List<ContentChangeEvent> events = List.of(emptyListDto, creationEvent, updateEvent);
        final List<ContentChangeEvent> errorEvents = List.of(nullListDto, nullOperationDto);

        for (final ContentChangeEvent event : events) {
            //invoke method under test
            assertDoesNotThrow(() -> quizService.deleteQuizzesWhenQuizContentIsDeleted(event));
            verify(quizRepository, never()).findAllById(any());
            verify(quizRepository, never()).deleteAllInBatch(any());
        }

        for (final ContentChangeEvent errorEvent : errorEvents) {
            //invoke method under test
            assertThrows(IncompleteEventMessageException.class, () -> quizService.deleteQuizzesWhenQuizContentIsDeleted(errorEvent));
            verify(quizRepository, never()).findAllById(any());
            verify(quizRepository, never()).deleteAllInBatch(any());
        }

    }

    @Test
    void publishProgressRandomModeTest() {
        final UUID assessmentId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();


        // create Database entities
        final List<QuestionEntity> questions = TestData.createDummyQuestions();

        final QuizEntity quizEntity = QuizEntity.builder().assessmentId(assessmentId)
                .questionPool(questions)
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .requiredCorrectAnswers(1)
                .numberOfRandomlySelectedQuestions(2).build();

        // create Inputs
        final QuestionCompletedInput inputQuestion = QuestionCompletedInput.builder()
                .setQuestionId(questions.get(0).getId())
                .setCorrect(true)
                .setUsedHint(false)
                .build();
        final QuestionCompletedInput inputQuestion2 = QuestionCompletedInput.builder()
                .setQuestionId(questions.get(1).getId())
                .setCorrect(true)
                .setUsedHint(false)
                .build();

        final QuizCompletedInput quizCompletedInput = QuizCompletedInput.builder()
                .setQuizId(assessmentId)
                .setCompletedQuestions(List.of(inputQuestion, inputQuestion2))
                .build();

        // create expected Progress event
        final ContentProgressedEvent expectedUserProgressLogEvent = ContentProgressedEvent.builder()
                .userId(userId)
                .contentId(assessmentId)
                .hintsUsed(0)
                .success(true)
                .timeToComplete(null)
                .correctness(2.0 / quizEntity.getNumberOfRandomlySelectedQuestions())
                .build();
        final QuizCompletionFeedback expectedQuizCompletionFeedback = QuizCompletionFeedback.builder()
                .setSuccess(true)
                .setHintsUsed(0)
                .setCorrectness(2.0 / quizEntity.getNumberOfRandomlySelectedQuestions())
                .build();
        //mock repository
        when(quizRepository.findById(assessmentId)).thenReturn(Optional.of(quizEntity));
        doNothing().when(topicPublisher).notifyUserWorkedOnContent(any());
        when(quizRepository.save(any())).thenReturn(quizEntity);

        // invoke method under test
        final QuizCompletionFeedback actualFeedback = quizService.publishProgress(quizCompletedInput, userId);

        assertThat(actualFeedback, is(expectedQuizCompletionFeedback));

        verify(quizRepository, times(1)).findById(assessmentId);
        verify(quizRepository, times(1)).save(any());
        verify(topicPublisher, times(1)).notifyUserWorkedOnContent(expectedUserProgressLogEvent);
    }

    @Test
    void publishProgressOrderedModeTest() {
        //init
        final UUID assessmentId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();

        // create Database entities
        final List<QuestionEntity> questions = TestData.createDummyQuestions();

        final QuizEntity quizEntity = QuizEntity.builder().assessmentId(assessmentId)
                .questionPool(questions)
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .requiredCorrectAnswers(2)
                .numberOfRandomlySelectedQuestions(2).build();

        // create Inputs
        final QuestionCompletedInput inputQuestion = QuestionCompletedInput.builder()
                .setQuestionId(questions.get(0).getId())
                .setCorrect(true)
                .setUsedHint(false)
                .build();
        final QuestionCompletedInput inputQuestion2 = QuestionCompletedInput.builder()
                .setQuestionId(questions.get(1).getId())
                .setCorrect(false)
                .setUsedHint(true)
                .build();

        final QuizCompletedInput quizCompletedInput = QuizCompletedInput.builder()
                .setQuizId(assessmentId)
                .setCompletedQuestions(List.of(inputQuestion, inputQuestion2))
                .build();

        // create expected Progress event
        final ContentProgressedEvent expectedUserProgressLogEvent = ContentProgressedEvent.builder()
                .userId(userId)
                .contentId(assessmentId)
                .hintsUsed(1)
                .success(false)
                .timeToComplete(null)
                .correctness(1.0 / quizEntity.getQuestionPool().size())
                .build();
        final QuizCompletionFeedback expectedQuizCompletionFeedback = QuizCompletionFeedback.builder()
                .setCorrectness(1.0 / quizEntity.getQuestionPool().size())
                .setHintsUsed(1)
                .setSuccess(false)
                .build();

        //mock repository
        when(quizRepository.findById(assessmentId)).thenReturn(Optional.of(quizEntity));
        when(quizRepository.save(any())).thenReturn(quizEntity);
        doNothing().when(topicPublisher).notifyUserWorkedOnContent(any());

        // invoke method under test
        final QuizCompletionFeedback actualFeedback = quizService.publishProgress(quizCompletedInput, userId);

        assertThat(actualFeedback, is(expectedQuizCompletionFeedback));

        verify(quizRepository, times(1)).findById(assessmentId);
        verify(quizRepository, times(1)).save(any());
        verify(topicPublisher, times(1)).notifyUserWorkedOnContent(expectedUserProgressLogEvent);

    }

    @Test
    void testCalculateCorrectnessEdgeCases() {

        final var actualWithZeroCorrectAnswers = quizService.calculateCorrectness(0.0, QuizEntity.builder().build());
        assertThat(actualWithZeroCorrectAnswers, is(0.0));

        final var quizEntityWithZeroQuestions = QuizEntity.builder()
                .questionPool(new ArrayList<>())
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .build();

        final var actualWithZeroQuestions = quizService.calculateCorrectness(1.0, quizEntityWithZeroQuestions);
        assertThat(actualWithZeroQuestions, is(1.0));

        final var quizEntityWithRandomlySelectedQuestionsZero = QuizEntity.builder()
                .questionPool(List.of(MultipleChoiceQuestionEntity.builder().build()))
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(0)
                .build();
        final var actualWithRandomlySelectedQuestionsZero
                = quizService.calculateCorrectness(1.0, quizEntityWithRandomlySelectedQuestionsZero);

        assertThat(actualWithRandomlySelectedQuestionsZero, is(1.0));

        final var quizEntityWithRandomlySelectedQuestionsNull = QuizEntity.builder()
                .questionPool(List.of(MultipleChoiceQuestionEntity.builder().build(),
                        MultipleChoiceQuestionEntity.builder().build()))
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(null)
                .build();

        final var actualWithRandomlySelectedQuestionsNull
                = quizService.calculateCorrectness(1.0, quizEntityWithRandomlySelectedQuestionsNull);

        assertThat(actualWithRandomlySelectedQuestionsNull, is(0.5));
    }

}