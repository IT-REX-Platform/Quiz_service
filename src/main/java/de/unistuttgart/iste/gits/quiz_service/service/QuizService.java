package de.unistuttgart.iste.gits.quiz_service.service;

import de.unistuttgart.iste.gits.common.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.common.event.ContentChangeEvent;
import de.unistuttgart.iste.gits.common.event.ContentProgressedEvent;
import de.unistuttgart.iste.gits.common.event.CrudOperation;
import de.unistuttgart.iste.gits.common.exception.IncompleteEventMessageException;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuestionStatisticEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.mapper.QuizMapper;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import de.unistuttgart.iste.gits.quiz_service.validation.QuizValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static de.unistuttgart.iste.gits.common.util.GitsCollectionUtils.count;
import static de.unistuttgart.iste.gits.common.util.GitsCollectionUtils.countAsInt;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final QuizValidator quizValidator;
    private final TopicPublisher topicPublisher;

    /**
     * Returns all quizzes for the given assessment ids.
     * If an assessment id does not exist, the corresponding quiz is null.
     *
     * @param assessmentIds the assessment ids
     * @return the (nullable) quizzes
     */
    public List<Quiz> findQuizzesByAssessmentIds(final List<UUID> assessmentIds) {
        return assessmentIds.stream()
                .map(quizRepository::findById)
                // map to dto or null if the quiz does not exist
                .map(optionalQuiz -> optionalQuiz.map(quizMapper::entityToDto).orElse(null))
                .toList();
    }

    /**
     * Creates a new quiz.
     *
     * @param courseId     the id of the course the quiz belongs to
     *                     (must be the same as the course id of the assessment)
     * @param assessmentId the id of the assessment the quiz belongs to
     * @param input        the quiz to create
     * @return the created quiz
     * @throws ValidationException if the quiz is invalid according to
     *                             {@link QuizValidator#validateCreateQuizInput(CreateQuizInput)}.
     */
    public Quiz createQuiz(final UUID courseId, final UUID assessmentId, final CreateQuizInput input) {
        quizValidator.validateCreateQuizInput(input);

        final QuizEntity entity = quizMapper.createQuizInputToEntity(input);
        entity.setAssessmentId(assessmentId);
        entity.setCourseId(courseId);

        final QuizEntity savedEntity = quizRepository.save(entity);
        return quizMapper.entityToDto(savedEntity);
    }

    /**
     * Deletes a quiz.
     *
     * @param id the id of the quiz to delete
     * @return the id of the deleted quiz if the deletion was successful
     * @throws EntityNotFoundException if the quiz does not exist
     */
    public UUID deleteQuiz(final UUID id) {
        requireQuizExists(id);

        quizRepository.deleteById(id);

        return id;
    }

    /**
     * Adds a multiple choice question to a quiz.
     *
     * @param quizId the id of the quiz
     * @param input  the question to add
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz does not exist
     * @throws ValidationException     if input is invalid according to
     *                                 {@link QuizValidator#validateCreateMultipleChoiceQuestionInput(CreateMultipleChoiceQuestionInput)}
     */
    public Quiz addMultipleChoiceQuestion(final UUID quizId, final CreateMultipleChoiceQuestionInput input) {
        quizValidator.validateCreateMultipleChoiceQuestionInput(input);

        return addQuestion(quizId, input, input.getNumber(), quizMapper::multipleChoiceQuestionInputToEntity);
    }

    /**
     * Updates a multiple choice question in a quiz.
     *
     * @param quizId the id of the quiz
     * @param input  the updated question
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz or the question does not exist
     * @throws ValidationException     if there is no correct answer
     */
    public Quiz updateMultipleChoiceQuestion(final UUID quizId, final UpdateMultipleChoiceQuestionInput input) {
        quizValidator.validateUpdateMultipleChoiceQuestionInput(input);

        return updateQuestion(quizId, input, input.getId(), quizMapper::multipleChoiceQuestionInputToEntity);
    }

    /**
     * Adds a cloze question to a quiz.
     *
     * @param quizId the id of the quiz
     * @param input  the question to add
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz does not exist
     * @throws ValidationException     if input is invalid, see
     *                                 {@link QuizValidator#validateCreateClozeQuestionInput(CreateClozeQuestionInput)}
     */
    public Quiz addClozeQuestion(final UUID quizId, final CreateClozeQuestionInput input) {
        quizValidator.validateCreateClozeQuestionInput(input);

        return addQuestion(quizId, input, input.getNumber(), quizMapper::clozeQuestionInputToEntity);
    }

    /**
     * Updates a cloze question in a quiz.
     *
     * @param quizId the id of the quiz
     * @param input  the updated question
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz or the question does not exist
     * @throws ValidationException     if input is invalid,
     *                                 see {@link QuizValidator#validateUpdateClozeQuestionInput(UpdateClozeQuestionInput)}
     */
    public Quiz updateClozeQuestion(final UUID quizId, final UpdateClozeQuestionInput input) {
        quizValidator.validateUpdateClozeQuestionInput(input);

        return updateQuestion(quizId, input, input.getId(), quizMapper::clozeQuestionInputToEntity);
    }

    /**
     * Adds an association question to a quiz.
     *
     * @param assessmentId the id of the quiz
     * @param input        the question to add
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz does not exist
     * @throws ValidationException     if invalid
     */
    public Quiz addAssociationQuestion(final UUID assessmentId, final CreateAssociationQuestionInput input) {
        quizValidator.validateCreateAssociationQuestionInput(input);

        return addQuestion(assessmentId, input, input.getNumber(), quizMapper::associationQuestionInputToEntity);
    }

    /**
     * Updates an association question in a quiz.
     *
     * @param assessmentId the id of the quiz
     * @param input        the updated question
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz or the question does not exist
     * @throws ValidationException     if invalid
     */
    public Quiz updateAssociationQuestion(final UUID assessmentId, final UpdateAssociationQuestionInput input) {
        quizValidator.validateUpdateAssociationQuestionInput(input);

        return updateQuestion(assessmentId, input, input.getId(), quizMapper::associationQuestionInputToEntity);
    }

    /**
     * Adds an exact answer question to a quiz.
     *
     * @param quizId the id of the quiz
     * @param input  the question to add
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz does not exist
     */
    public Quiz addExactAnswerQuestion(final UUID quizId, final CreateExactAnswerQuestionInput input) {
        return addQuestion(quizId, input, input.getNumber(), quizMapper::exactAnswerQuestionInputToEntity);
    }

    /**
     * Updates an exact answer question in a quiz.
     *
     * @param quizId the id of the quiz
     * @param input  the updated question
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz or the question does not exist
     */
    public Quiz updateExactAnswerQuestion(final UUID quizId, final UpdateExactAnswerQuestionInput input) {
        return updateQuestion(quizId, input, input.getId(), quizMapper::exactAnswerQuestionInputToEntity);
    }

    /**
     * Adds a numeric question to a quiz.
     *
     * @param quizId the id of the quiz
     * @param input  the question to add
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz does not exist
     */
    public Quiz addNumericQuestion(final UUID quizId, final CreateNumericQuestionInput input) {
        return addQuestion(quizId, input, input.getNumber(), quizMapper::numericQuestionInputToEntity);
    }

    /**
     * Updates a numeric question in a quiz.
     *
     * @param quizId the id of the quiz
     * @param input  the updated question
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz or the question does not exist
     */
    public Quiz updateNumericQuestion(final UUID quizId, final UpdateNumericQuestionInput input) {
        return updateQuestion(quizId, input, input.getId(), quizMapper::numericQuestionInputToEntity);
    }

    /**
     * Adds a self assessment question to a quiz.
     *
     * @param quizId the id of the quiz
     * @param input  the question to add
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz does not exist
     */
    public Quiz addSelfAssessmentQuestion(final UUID quizId, final CreateSelfAssessmentQuestionInput input) {
        return addQuestion(quizId, input, input.getNumber(), quizMapper::selfAssessmentQuestionInputToEntity);
    }

    /**
     * Updates a self assessment question in a quiz.
     *
     * @param quizId the id of the quiz
     * @param input  the updated question
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz or the question does not exist
     */
    public Quiz updateSelfAssessmentQuestion(final UUID quizId, final UpdateSelfAssessmentQuestionInput input) {
        return updateQuestion(quizId, input, input.getId(), quizMapper::selfAssessmentQuestionInputToEntity);
    }

    /**
     * Updates a question in a quiz.
     *
     * @param quizId     the id of the quiz
     * @param input      the updated question
     * @param questionId the id of the question to update
     * @param mapping    the mapping function that maps the input to a question entity
     * @param <I>        the type of the question input, e.g. {@link UpdateMultipleChoiceQuestionInput}
     * @return the modified quiz
     */
    private <I> Quiz updateQuestion(final UUID quizId,
                                    final I input,
                                    final UUID questionId,
                                    final Function<I, ? extends QuestionEntity> mapping) {
        return modifyQuiz(quizId, entity -> {
            final QuestionEntity questionEntity = getQuestionInQuizById(entity, questionId);

            final int indexOfQuestion = entity.getQuestionPool().indexOf(questionEntity);

            final QuestionEntity newQuestionEntity = mapping.apply(input);
            newQuestionEntity.setNumber(questionEntity.getNumber());
            entity.getQuestionPool().set(indexOfQuestion, newQuestionEntity);
        });
    }

    /**
     * Adds a question to a quiz.
     *
     * @param quizId         the id of the quiz.
     * @param input          the question input that contains the question to add.
     * @param questionNumber the number of the question to add. If null, the number is assigned automatically.
     * @param mapping        the mapping function that maps the input to a question entity.
     * @param <I>            the type of the question input, e.g. {@link CreateMultipleChoiceQuestionInput}
     *
     * @implNote The second parameter questionNumber is necessary because the input types do not share a common interface,
     * and we cannot change the input types, as they are generated from the schema.
     * @return the modified quiz
     */
    private <I> Quiz addQuestion(final UUID quizId,
                                 final I input,
                                 final Integer questionNumber,
                                 final Function<I, QuestionEntity> mapping) {
        return modifyQuiz(quizId, entity -> {
            // use the given number or assign a new one
            final int number = Optional.ofNullable(questionNumber)
                    .orElseGet(() -> assignNumber(entity));

            quizValidator.checkNumberIsUnique(entity, number);

            final QuestionEntity questionEntity = mapping.apply(input);
            questionEntity.setNumber(number);
            entity.getQuestionPool().add(questionEntity);
        });
    }

    /**
     * Removes a question from a quiz.
     *
     * @param quizId the id of the quiz
     * @param number the number of the question to remove
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz does not exist
     */
    public Quiz removeQuestion(final UUID quizId, final int number) {
        return modifyQuiz(quizId, entity -> {
            final QuestionEntity questionEntity = getQuestionInQuizByNumber(entity, number);
            entity.getQuestionPool().remove(questionEntity);

            // decrease the number of all questions with a higher number
            entity.getQuestionPool().stream()
                    .filter(q -> q.getNumber() > number)
                    .forEach(q -> q.setNumber(q.getNumber() - 1));
        });
    }

    /**
     * Switches the position of two questions in a quiz.
     *
     * @param quizId       the id of the quiz
     * @param firstNumber  the number of the first question
     * @param secondNumber the number of the second question
     * @return the modified quiz
     * @throws EntityNotFoundException if the quiz does not exist
     */
    public Quiz switchQuestions(final UUID quizId, final int firstNumber, final int secondNumber) {
        return modifyQuiz(quizId, entity -> {
            final QuestionEntity firstQuestionEntity = getQuestionInQuizByNumber(entity, firstNumber);
            final QuestionEntity secondQuestionEntity = getQuestionInQuizByNumber(entity, secondNumber);

            firstQuestionEntity.setNumber(secondNumber);
            secondQuestionEntity.setNumber(firstNumber);

            final int indexOfFirstQuestion = entity.getQuestionPool().indexOf(firstQuestionEntity);
            final int indexOfSecondQuestion = entity.getQuestionPool().indexOf(secondQuestionEntity);
            Collections.swap(entity.getQuestionPool(), indexOfFirstQuestion, indexOfSecondQuestion);
        });
    }

    public Quiz setRequiredCorrectAnswers(final UUID quizId, final int requiredCorrectAnswers) {
        return modifyQuiz(quizId, entity -> entity.setRequiredCorrectAnswers(requiredCorrectAnswers));
    }

    public Quiz setQuestionPoolingMode(final UUID quizId, final QuestionPoolingMode questionPoolingMode) {
        return modifyQuiz(quizId, entity -> entity.setQuestionPoolingMode(questionPoolingMode));
    }

    public Quiz setNumberOfRandomlySelectedQuestions(final UUID quizId, final int numberOfRandomlySelectedQuestions) {
        return modifyQuiz(quizId, entity -> entity.setNumberOfRandomlySelectedQuestions(numberOfRandomlySelectedQuestions));
    }

    /**
     * Returns the quiz with the given id or throws an exception if the quiz does not exist.
     *
     * @param id the id of the quiz
     * @return the quiz entity
     * @throws EntityNotFoundException if the quiz does not exist
     */
    public QuizEntity requireQuizExists(final UUID id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz with id " + id + " not found"));
    }

    /**
     * Modifies a quiz by applying the given modifier to the quiz entity
     * and saves the modified entity to the database.
     *
     * @param quiz     the quiz to modify
     * @param modifier the modifier to apply to the quiz entity
     * @return the modified quiz as DTO
     * @throws EntityNotFoundException if the quiz does not exist
     */
    private Quiz modifyQuiz(final UUID quiz, final Consumer<QuizEntity> modifier) {
        final QuizEntity entity = requireQuizExists(quiz);

        modifier.accept(entity);

        final QuizEntity savedEntity = quizRepository.save(entity);
        return quizMapper.entityToDto(savedEntity);
    }

    private int assignNumber(final QuizEntity entity) {
        if (entity.getQuestionPool().isEmpty()) {
            return 1;
        }
        // the question pool is sorted by number, so we can just take the last number and add 1
        return entity.getQuestionPool().get(entity.getQuestionPool().size() - 1).getNumber() + 1;
    }

    private QuestionEntity getQuestionInQuizById(final QuizEntity quizEntity, final UUID questionId) {
        return quizEntity.getQuestionPool().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(MessageFormat.format(
                                "Question with id {0} not found in quiz with id {1}.",
                                questionId, quizEntity.getAssessmentId())));
    }

    private QuestionEntity getQuestionInQuizByNumber(final QuizEntity quizEntity, final int number) {
        return quizEntity.getQuestionPool().stream()
                .filter(q -> q.getNumber() == number)
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(MessageFormat.format(
                                "Question with number {0} not found in quiz with id {1}.",
                                number, quizEntity.getAssessmentId())));
    }

    /**
     * removes all Quizzes when linked Content gets deleted
     *
     * @param dto event object containing changes to content
     */
    public void deleteQuizzesWhenQuizContentIsDeleted(final ContentChangeEvent dto) throws IncompleteEventMessageException {
        // validate event message
        checkCompletenessOfDto(dto);

        // only consider DELETE Operations
        if (!dto.getOperation().equals(CrudOperation.DELETE) || dto.getContentIds().isEmpty()) {
            return;
        }

        // delete all found quizzes
        quizRepository.deleteAllByIdInBatch(dto.getContentIds());
    }

    /**
     * helper function to make sure received event message is complete
     *
     * @param dto event message under evaluation
     * @throws NullPointerException if any of the fields are null
     */
    private void checkCompletenessOfDto(final ContentChangeEvent dto) throws IncompleteEventMessageException {
        if (dto.getOperation() == null || dto.getContentIds() == null) {
            throw new IncompleteEventMessageException(IncompleteEventMessageException.ERROR_INCOMPLETE_MESSAGE);
        }
    }


    /**
     * publishes user progress on a quiz to dapr topic
     *
     * @param input  user progress made
     * @param userId the user that made progress
     * @return quiz that was worked on
     */
    public QuizCompletionFeedback publishProgress(final QuizCompletedInput input, final UUID userId) {
        final QuizEntity quizEntity = requireQuizExists(input.getQuizId());

        updateQuestionStatistics(input, userId, quizEntity);

        // count the number of questions that were answered correctly
        final long numbCorrectAnswers = count(input.getCompletedQuestions(), QuestionCompletedInput::getCorrect);

        final boolean success = numbCorrectAnswers >= quizEntity.getRequiredCorrectAnswers();
        final double correctness = calculateCorrectness(numbCorrectAnswers, quizEntity);
        final int hintsUsed = countAsInt(input.getCompletedQuestions(), QuestionCompletedInput::getUsedHint);

        // create new user progress event message
        final ContentProgressedEvent userProgressLogEvent = ContentProgressedEvent.builder()
                .userId(userId)
                .contentId(quizEntity.getAssessmentId())
                .hintsUsed(hintsUsed)
                .success(success)
                .timeToComplete(null)
                .correctness(correctness)
                .build();

        // publish new user progress event message
        topicPublisher.notifyUserWorkedOnContent(userProgressLogEvent);
        return QuizCompletionFeedback.builder()
                .setSuccess(success)
                .setCorrectness(correctness)
                .setHintsUsed(hintsUsed)
                .build();
    }

    /**
     * Method that updates Statistics for a question. Any changes are discarded if any of the received question IDs do not exist.
     *
     * @param input      Object containing the IDs of the answered questions and their correctness
     * @param userId     ID of user completing the quiz
     * @param quizEntity quiz, questions are a part of
     */
    private void updateQuestionStatistics(final QuizCompletedInput input, final UUID userId, final QuizEntity quizEntity) {

        for (final QuestionCompletedInput completedQuestion : input.getCompletedQuestions()) {

            // find question in quiz. throws an exception if question can not be found
            final QuestionEntity questionEntity = getQuestionInQuizById(quizEntity, completedQuestion.getQuestionId());
            final int index = quizEntity.getQuestionPool().indexOf(questionEntity);

            // create new Question Statistic
            final QuestionStatisticEntity newQuestionStatistic = QuestionStatisticEntity.builder()
                    .questionId(questionEntity.getId())
                    .userId(userId)
                    .answeredCorrectly(completedQuestion.getCorrect())
                    .build();

            questionEntity.getQuestionStatistics().add(newQuestionStatistic);

            //overwrite outdated question
            quizEntity.getQuestionPool().set(index, questionEntity);
        }

        //persist changes
        quizRepository.save(quizEntity);

    }

    /**
     * Calculates the correctness value for a quiz
     *
     * @param correctAnswers number of correct answers
     * @param quizEntity     quizEntity source
     * @return calculated correctness value
     */
    protected double calculateCorrectness(final double correctAnswers, final QuizEntity quizEntity) {
        if (correctAnswers == 0.0) {
            // prevent division by zero
            return correctAnswers;
        }

        // in RANDOM mode, the number of questions is not the size of the question pool
        // but the number of randomly selected questions
        if (quizEntity.getQuestionPoolingMode().equals(QuestionPoolingMode.RANDOM)
            && quizEntity.getNumberOfRandomlySelectedQuestions() != null) {

            if (quizEntity.getNumberOfRandomlySelectedQuestions() == 0) {
                // prevent division by zero
                return 1.0;
            }

            return correctAnswers / quizEntity.getNumberOfRandomlySelectedQuestions();

        } else if (quizEntity.getQuestionPool().isEmpty()) {
            return 1.0;
        } else {
            return correctAnswers / quizEntity.getQuestionPool().size();
        }

    }
}
