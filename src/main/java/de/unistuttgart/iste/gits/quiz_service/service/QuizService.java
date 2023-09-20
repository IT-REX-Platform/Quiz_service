package de.unistuttgart.iste.gits.quiz_service.service;

import de.unistuttgart.iste.gits.common.event.*;
import de.unistuttgart.iste.gits.common.exception.IncompleteEventMessageException;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.*;
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
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

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
    public List<Quiz> findQuizzesByAssessmentIds(List<UUID> assessmentIds) {
        return assessmentIds.stream()
                .map(quizRepository::findById)
                .map(optionalQuiz -> optionalQuiz.map(quizMapper::entityToDto))
                .map(optionalQuiz -> optionalQuiz.orElse(null))
                .toList();
    }

    /**
     * Creates a new quiz.
     *
     * @param input the quiz to create
     * @return the created quiz
     * @throws ValidationException if the quiz is invalid according to
     *                             {@link QuizValidator#validateCreateQuizInput(CreateQuizInput)}.
     */
    public Quiz createQuiz(UUID assessmentId, CreateQuizInput input) {
        quizValidator.validateCreateQuizInput(input);

        QuizEntity entity = quizMapper.createQuizInputToEntity(input);
        entity.setAssessmentId(assessmentId);

        QuizEntity savedEntity = quizRepository.save(entity);
        return quizMapper.entityToDto(savedEntity);
    }

    /**
     * Deletes a quiz.
     *
     * @param id the id of the quiz to delete
     * @return the id of the deleted quiz if the deletion was successful
     * @throws EntityNotFoundException if the quiz does not exist
     */
    public UUID deleteQuiz(UUID id) {
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
    public Quiz addMultipleChoiceQuestion(UUID quizId, CreateMultipleChoiceQuestionInput input) {
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
    public Quiz updateMultipleChoiceQuestion(UUID quizId, UpdateMultipleChoiceQuestionInput input) {
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
    public Quiz addClozeQuestion(UUID quizId, CreateClozeQuestionInput input) {
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
    public Quiz updateClozeQuestion(UUID quizId, UpdateClozeQuestionInput input) {
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
    public Quiz addAssociationQuestion(UUID assessmentId, CreateAssociationQuestionInput input) {
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
    public Quiz updateAssociationQuestion(UUID assessmentId, UpdateAssociationQuestionInput input) {
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
    public Quiz addExactAnswerQuestion(UUID quizId, CreateExactAnswerQuestionInput input) {
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
    public Quiz updateExactAnswerQuestion(UUID quizId, UpdateExactAnswerQuestionInput input) {
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
    public Quiz addNumericQuestion(UUID quizId, CreateNumericQuestionInput input) {
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
    public Quiz updateNumericQuestion(UUID quizId, UpdateNumericQuestionInput input) {
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
    public Quiz addSelfAssessmentQuestion(UUID quizId, CreateSelfAssessmentQuestionInput input) {
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
    public Quiz updateSelfAssessmentQuestion(UUID quizId, UpdateSelfAssessmentQuestionInput input) {
        return updateQuestion(quizId, input, input.getId(), quizMapper::selfAssessmentQuestionInputToEntity);
    }

    private <I, E extends QuestionEntity> Quiz updateQuestion(UUID quizId,
                                                              I input,
                                                              UUID questionId,
                                                              Function<I, E> mapping) {
        return modifyQuiz(quizId, entity -> {
            QuestionEntity questionEntity = getQuestionInQuizById(entity, questionId);

            int indexOfQuestion = entity.getQuestionPool().indexOf(questionEntity);
            QuestionEntity newQuestionEntity = mapping.apply(input);
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
     * @param <E>            the type of the question entity, e.g. {@link MultipleChoiceQuestionEntity}
     * @return the modified quiz
     */
    private <I, E extends QuestionEntity> Quiz addQuestion(UUID quizId,
                                                           I input,
                                                           Integer questionNumber,
                                                           Function<I, E> mapping) {
        return modifyQuiz(quizId, entity -> {
            int number = questionNumber == null ? assignNumber(entity) : questionNumber;
            quizValidator.checkNumberIsUnique(entity, number);

            E questionEntity = mapping.apply(input);
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
    public Quiz removeQuestion(UUID quizId, int number) {
        return modifyQuiz(quizId, entity -> {
            QuestionEntity questionEntity = getQuestionInQuizByNumber(entity, number);
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
    public Quiz switchQuestions(UUID quizId, int firstNumber, int secondNumber) {
        return modifyQuiz(quizId, entity -> {
            QuestionEntity firstQuestionEntity = getQuestionInQuizByNumber(entity, firstNumber);
            QuestionEntity secondQuestionEntity = getQuestionInQuizByNumber(entity, secondNumber);

            firstQuestionEntity.setNumber(secondNumber);
            secondQuestionEntity.setNumber(firstNumber);

            int indexOfFirstQuestion = entity.getQuestionPool().indexOf(firstQuestionEntity);
            int indexOfSecondQuestion = entity.getQuestionPool().indexOf(secondQuestionEntity);
            Collections.swap(entity.getQuestionPool(), indexOfFirstQuestion, indexOfSecondQuestion);
        });
    }

    public Quiz setRequiredCorrectAnswers(UUID quizId, int requiredCorrectAnswers) {
        return modifyQuiz(quizId, entity -> entity.setRequiredCorrectAnswers(requiredCorrectAnswers));
    }

    public Quiz setQuestionPoolingMode(UUID quizId, QuestionPoolingMode questionPoolingMode) {
        return modifyQuiz(quizId, entity -> entity.setQuestionPoolingMode(questionPoolingMode));
    }

    public Quiz setNumberOfRandomlySelectedQuestions(UUID quizId, int numberOfRandomlySelectedQuestions) {
        return modifyQuiz(quizId, entity -> entity.setNumberOfRandomlySelectedQuestions(numberOfRandomlySelectedQuestions));
    }

    /**
     * Returns the quiz with the given id or throws an exception if the quiz does not exist.
     *
     * @param id the id of the quiz
     * @return the quiz entity
     * @throws EntityNotFoundException if the quiz does not exist
     */
    public QuizEntity requireQuizExists(UUID id) {
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
    private Quiz modifyQuiz(UUID quiz, Consumer<QuizEntity> modifier) {
        QuizEntity entity = requireQuizExists(quiz);

        modifier.accept(entity);

        QuizEntity savedEntity = quizRepository.save(entity);
        return quizMapper.entityToDto(savedEntity);
    }

    private int assignNumber(QuizEntity entity) {
        if (entity.getQuestionPool().isEmpty()) {
            return 1;
        }
        return entity.getQuestionPool().get(entity.getQuestionPool().size() - 1).getNumber() + 1;
    }

    private QuestionEntity getQuestionInQuizById(QuizEntity quizEntity, UUID questionId) {
        return quizEntity.getQuestionPool().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(MessageFormat.format(
                                "Question with id {0} not found in quiz with id {1}.",
                                questionId, quizEntity.getAssessmentId())));
    }

    private QuestionEntity getQuestionInQuizByNumber(QuizEntity quizEntity, int number) {
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
    public void deleteQuizzesWhenQuizContentIsDeleted(ContentChangeEvent dto) throws IncompleteEventMessageException {
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
    private void checkCompletenessOfDto(ContentChangeEvent dto) throws IncompleteEventMessageException {
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
    public QuizCompletionFeedback publishProgress(QuizCompletedInput input, UUID userId) {
        QuizEntity quizEntity = quizRepository.getReferenceById(input.getQuizId());

        updateQuestionStatistics(input, userId, quizEntity);

        // count the number of questions that were answered correctly
        long numbCorrectAnswers = input.getCompletedQuestions().stream().filter(QuestionCompletedInput::getCorrect).count();

        boolean success = numbCorrectAnswers >= quizEntity.getRequiredCorrectAnswers();
        double correctness = calcCorrectness(numbCorrectAnswers, quizEntity);
        int hintsUsed = (int) input.getCompletedQuestions().stream().filter(QuestionCompletedInput::getUsedHint).count();

        // create new user progress event message
        UserProgressLogEvent userProgressLogEvent = UserProgressLogEvent.builder()
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
    private void updateQuestionStatistics(QuizCompletedInput input, UUID userId, QuizEntity quizEntity) {

        for (QuestionCompletedInput completedQuestion : input.getCompletedQuestions()) {

            // find question in quiz. throws an exception if question can not be found
            QuestionEntity questionEntity = getQuestionInQuizById(quizEntity, completedQuestion.getQuestionId());
            int index = quizEntity.getQuestionPool().indexOf(questionEntity);

            // create new Question Statistic
            QuestionStatisticEntity newQuestionStatistic = QuestionStatisticEntity.builder()
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
    protected double calcCorrectness(double correctAnswers, QuizEntity quizEntity) {
        if (correctAnswers == 0.0) {
            return correctAnswers;
        }

        if (quizEntity.getQuestionPoolingMode().equals(QuestionPoolingMode.RANDOM)
            && quizEntity.getNumberOfRandomlySelectedQuestions() != null) {

            if (quizEntity.getNumberOfRandomlySelectedQuestions() == 0) {
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
