package de.unistuttgart.iste.gits.quiz_service.service;

import de.unistuttgart.iste.gits.common.event.*;
import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEmbeddable;
import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEntity;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.mapper.QuizMapper;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import de.unistuttgart.iste.gits.quiz_service.validation.QuizValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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


        // create Database entities
        List<QuestionEntity> questions = createDummyQuestions();

        QuizEntity quizEntity = QuizEntity.builder().assessmentId(assessmentId)
                .questionPool(questions)
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .requiredCorrectAnswers(1)
                .numberOfRandomlySelectedQuestions(2).build();

        // create Inputs
        QuestionCompletedInput inputQuestion = QuestionCompletedInput.builder()
                .setQuestionId(questions.get(0).getId())
                .setCorrect(true)
                .setUsedHint(false)
                .build();
        QuestionCompletedInput inputQuestion2 = QuestionCompletedInput.builder()
                .setQuestionId(questions.get(1).getId())
                .setCorrect(true)
                .setUsedHint(false)
                .build();

        QuizCompletedInput quizCompletedInput = QuizCompletedInput.builder()
                .setQuizId(assessmentId)
                .setCompletedQuestions(List.of(inputQuestion, inputQuestion2))
                .build();

        // create expected Progress event
        UserProgressLogEvent expectedUserProgressLogEvent = UserProgressLogEvent.builder()
                .userId(userId)
                .contentId(assessmentId)
                .hintsUsed(0)
                .success(true)
                .timeToComplete(null)
                .correctness(2.0 / quizEntity.getNumberOfRandomlySelectedQuestions())
                .build();

        //mock repository
        when(quizRepository.getReferenceById(assessmentId)).thenReturn(quizEntity);
        doNothing().when(topicPublisher).notifyUserWorkedOnContent(any());
        when(quizRepository.save(any())).thenReturn(quizEntity);

        // invoke method under test
        quizService.publishProgress(quizCompletedInput, userId);

        verify(quizRepository, times(1)).getReferenceById(assessmentId);
        verify(quizRepository, times(1)).save(any());
        verify(topicPublisher, times(1)).notifyUserWorkedOnContent(expectedUserProgressLogEvent);

    }

    @Test
    void publishProgressOrderedModeTest() {
        //init
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // create Database entities
        List<QuestionEntity> questions = createDummyQuestions();

        QuizEntity quizEntity = QuizEntity.builder().assessmentId(assessmentId)
                .questionPool(questions)
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .requiredCorrectAnswers(2)
                .numberOfRandomlySelectedQuestions(2).build();

        // create Inputs
        QuestionCompletedInput inputQuestion = QuestionCompletedInput.builder()
                .setQuestionId(questions.get(0).getId())
                .setCorrect(true)
                .setUsedHint(false)
                .build();
        QuestionCompletedInput inputQuestion2 = QuestionCompletedInput.builder()
                .setQuestionId(questions.get(1).getId())
                .setCorrect(false)
                .setUsedHint(true)
                .build();

        QuizCompletedInput quizCompletedInput = QuizCompletedInput.builder()
                .setQuizId(assessmentId)
                .setCompletedQuestions(List.of(inputQuestion, inputQuestion2))
                .build();

        // create expected Progress event
        UserProgressLogEvent expectedUserProgressLogEvent = UserProgressLogEvent.builder()
                .userId(userId)
                .contentId(assessmentId)
                .hintsUsed(1)
                .success(false)
                .timeToComplete(null)
                .correctness(1.0 / quizEntity.getQuestionPool().size())
                .build();

        //mock repository
        when(quizRepository.getReferenceById(assessmentId)).thenReturn(quizEntity);
        when(quizRepository.save(any())).thenReturn(quizEntity);
        doNothing().when(topicPublisher).notifyUserWorkedOnContent(any());

        // invoke method under test
        quizService.publishProgress(quizCompletedInput, userId);

        verify(quizRepository, times(1)).getReferenceById(assessmentId);
        verify(quizRepository, times(1)).save(any());
        verify(topicPublisher, times(1)).notifyUserWorkedOnContent(expectedUserProgressLogEvent);

    }

    @Test
    void testCalculateCorrectnessEdgeCases() {

        var actualWithZeroCorrectAnswers = quizService.calcCorrectness(0.0, QuizEntity.builder().build());
        assertThat(actualWithZeroCorrectAnswers, is(0.0));

        var quizEntityWithZeroQuestions = QuizEntity.builder()
                .questionPool(new ArrayList<>())
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .build();

        var actualWithZeroQuestions = quizService.calcCorrectness(1.0, quizEntityWithZeroQuestions);
        assertThat(actualWithZeroQuestions, is(1.0));

        var quizEntityWithRandomlySelectedQuestionsZero = QuizEntity.builder()
                .questionPool(List.of(MultipleChoiceQuestionEntity.builder().build()))
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(0)
                .build();
        var actualWithRandomlySelectedQuestionsZero
                = quizService.calcCorrectness(1.0, quizEntityWithRandomlySelectedQuestionsZero);

        assertThat(actualWithRandomlySelectedQuestionsZero, is(1.0));

        var quizEntityWithRandomlySelectedQuestionsNull = QuizEntity.builder()
                .questionPool(List.of(MultipleChoiceQuestionEntity.builder().build(),
                        MultipleChoiceQuestionEntity.builder().build()))
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(null)
                .build();

        var actualWithRandomlySelectedQuestionsNull
                = quizService.calcCorrectness(1.0, quizEntityWithRandomlySelectedQuestionsNull);

        assertThat(actualWithRandomlySelectedQuestionsNull, is(0.5));
    }

    /**
     * creates some dummy multiple choice questions
     *
     * @return List of 2 Multiple Choice Question (database) Entities
     */
    private List<QuestionEntity> createDummyQuestions() {
        List<QuestionEntity> questions = new ArrayList<>();
        MultipleChoiceAnswerEmbeddable wrongAnswer = MultipleChoiceAnswerEmbeddable.builder()
                .answerText(new ResourceMarkdownEntity("Pick me! Pick Me!"))
                .correct(false)
                .feedback(new ResourceMarkdownEntity("Fell for it"))
                .build();
        MultipleChoiceAnswerEmbeddable correctAnswer = MultipleChoiceAnswerEmbeddable.builder()
                .answerText(new ResourceMarkdownEntity("No me!"))
                .correct(true)
                .feedback(new ResourceMarkdownEntity("Well done!"))
                .build();
        MultipleChoiceQuestionEntity questionEntity = MultipleChoiceQuestionEntity.builder()
                .id(UUID.randomUUID())
                .number(0)
                .type(QuestionType.MULTIPLE_CHOICE)
                .text(new ResourceMarkdownEmbeddable("This is a question"))
                .answers(List.of(wrongAnswer, correctAnswer))
                .hint(new ResourceMarkdownEntity("Wink Wink"))
                .build();
        MultipleChoiceQuestionEntity questionEntity2 = MultipleChoiceQuestionEntity.builder()
                .id(UUID.randomUUID())
                .number(0)
                .type(QuestionType.MULTIPLE_CHOICE)
                .text(new ResourceMarkdownEmbeddable("This is a question"))
                .answers(List.of(wrongAnswer, correctAnswer))
                .hint(new ResourceMarkdownEntity("Wink Wink"))
                .build();

        questions.add(questionEntity);
        questions.add(questionEntity2);

        return questions;
    }

}