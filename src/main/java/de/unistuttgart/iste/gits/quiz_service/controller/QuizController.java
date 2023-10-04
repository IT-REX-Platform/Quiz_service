package de.unistuttgart.iste.gits.quiz_service.controller;

import de.unistuttgart.iste.gits.common.exception.NoAccessToCourseException;
import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.gits.common.user_handling.UserCourseAccessValidator;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.service.QuizService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QuizController {

    private static final String QUIZ_MUTATION_NAME = "QuizMutation";

    private final QuizService quizService;

    @QueryMapping
    public List<Quiz> findQuizzesByAssessmentIds(@Argument final List<UUID> assessmentIds,
                                                 @ContextValue final LoggedInUser currentUser) {
        return quizService.findQuizzesByAssessmentIds(assessmentIds).stream()
                .map(quiz -> {
                    if (quiz == null) {
                        return null;
                    }
                    try {
                        UserCourseAccessValidator.validateUserHasAccessToCourse(currentUser,
                                LoggedInUser.UserRoleInCourse.STUDENT,
                                quiz.getCourseId());
                        return quiz;
                    } catch (final NoAccessToCourseException ex) {
                        return null;
                    }
                })
                .toList();
    }

    @MutationMapping(name = "_internal_noauth_createQuiz")
    public Quiz createQuiz(@Argument final UUID courseId, @Argument final UUID assessmentId, @Argument final CreateQuizInput input) {
        return quizService.createQuiz(courseId, assessmentId, input);
    }

    @MutationMapping
    public QuizMutation mutateQuiz(@Argument final UUID assessmentId,
                                   @ContextValue final LoggedInUser currentUser) {
        final Quiz quiz = quizService.findQuizzesByAssessmentIds(List.of(assessmentId)).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No quiz found for assessment id " + assessmentId));

        UserCourseAccessValidator.validateUserHasAccessToCourse(currentUser,
                LoggedInUser.UserRoleInCourse.ADMINISTRATOR,
                quiz.getCourseId());

        // this is basically an empty object, only serving as a parent for the nested mutations
        return new QuizMutation(assessmentId);
    }

    @MutationMapping
    public UUID deleteQuiz(@Argument final UUID assessmentId) {
        return quizService.deleteQuiz(assessmentId);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz addMultipleChoiceQuestion(@Argument final CreateMultipleChoiceQuestionInput input, final QuizMutation quizMutation) {
        return quizService.addMultipleChoiceQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz updateMultipleChoiceQuestion(@Argument final UpdateMultipleChoiceQuestionInput input, final QuizMutation quizMutation) {
        return quizService.updateMultipleChoiceQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz addClozeQuestion(@Argument final CreateClozeQuestionInput input, final QuizMutation quizMutation) {
        return quizService.addClozeQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz updateClozeQuestion(@Argument final UpdateClozeQuestionInput input, final QuizMutation quizMutation) {
        return quizService.updateClozeQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz addAssociationQuestion(@Argument final CreateAssociationQuestionInput input, final QuizMutation quizMutation) {
        return quizService.addAssociationQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz updateAssociationQuestion(@Argument final UpdateAssociationQuestionInput input, final QuizMutation quizMutation) {
        return quizService.updateAssociationQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz addExactAnswerQuestion(@Argument final CreateExactAnswerQuestionInput input, final QuizMutation quizMutation) {
        return quizService.addExactAnswerQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz updateExactAnswerQuestion(@Argument final UpdateExactAnswerQuestionInput input, final QuizMutation quizMutation) {
        return quizService.updateExactAnswerQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz addNumericQuestion(@Argument final CreateNumericQuestionInput input, final QuizMutation quizMutation) {
        return quizService.addNumericQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz updateNumericQuestion(@Argument final UpdateNumericQuestionInput input, final QuizMutation quizMutation) {
        return quizService.updateNumericQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz addSelfAssessmentQuestion(@Argument final CreateSelfAssessmentQuestionInput input, final QuizMutation quizMutation) {
        return quizService.addSelfAssessmentQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz updateSelfAssessmentQuestion(@Argument final UpdateSelfAssessmentQuestionInput input, final QuizMutation quizMutation) {
        return quizService.updateSelfAssessmentQuestion(quizMutation.getAssessmentId(), input);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz removeQuestion(@Argument final int number, final QuizMutation quizMutation) {
        return quizService.removeQuestion(quizMutation.getAssessmentId(), number);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz switchQuestions(@Argument final int firstNumber, @Argument final int secondNumber, final QuizMutation quizMutation) {
        return quizService.switchQuestions(quizMutation.getAssessmentId(), firstNumber, secondNumber);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz setRequiredCorrectAnswers(@Argument final int requiredCorrectAnswers, final QuizMutation quizMutation) {
        return quizService.setRequiredCorrectAnswers(quizMutation.getAssessmentId(), requiredCorrectAnswers);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz setQuestionPoolingMode(@Argument final QuestionPoolingMode questionPoolingMode, final QuizMutation quizMutation) {
        return quizService.setQuestionPoolingMode(quizMutation.getAssessmentId(), questionPoolingMode);
    }

    @SchemaMapping(typeName = QUIZ_MUTATION_NAME)
    public Quiz setNumberOfRandomlySelectedQuestions(@Argument final int numberOfRandomlySelectedQuestions, final QuizMutation quizMutation) {
        return quizService.setNumberOfRandomlySelectedQuestions(quizMutation.getAssessmentId(), numberOfRandomlySelectedQuestions);
    }

    @MutationMapping
    public QuizCompletionFeedback logQuizCompleted(@Argument final QuizCompletedInput input,
                                                   @ContextValue final LoggedInUser currentUser) {
        final UUID courseId = quizService.findQuizzesByAssessmentIds(List.of(input.getQuizId())).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No quiz found for quiz id " + input.getQuizId()))
                .getCourseId();


        UserCourseAccessValidator.validateUserHasAccessToCourse(currentUser,
                LoggedInUser.UserRoleInCourse.STUDENT,
                courseId);

        return quizService.publishProgress(input, currentUser.getId());
    }

}
