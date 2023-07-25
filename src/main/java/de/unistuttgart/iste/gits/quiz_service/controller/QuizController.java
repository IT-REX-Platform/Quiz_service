package de.unistuttgart.iste.gits.quiz_service.controller;

import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.service.QuizService;
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

    private final QuizService quizService;

    @QueryMapping
    public Quiz quizByAssessmentId(@Argument UUID id) {
        return quizService.getQuizByAssessmentId(id);
    }

    @QueryMapping
    public List<Quiz> quizzes() {
        return quizService.getAllQuizzes();
    }

    @MutationMapping
    public Quiz createQuiz(@Argument CreateQuizInput input) {
        return quizService.createQuiz(input);
    }

    @MutationMapping
    public QuizMutation modifyQuiz(@Argument UUID id) {
        // this is basically an empty object, only serving as a parent for the nested mutations
        return new QuizMutation(id);
    }

    @MutationMapping
    public UUID deleteQuiz(@Argument UUID id) {
        return quizService.deleteQuiz(id);
    }

    @SchemaMapping(typeName = "QuizMutation")
    public Quiz addMultipleChoiceQuestion(@Argument CreateMultipleChoiceQuestionInput input, QuizMutation quizMutation) {
        return quizService.addMultipleChoiceQuestion(quizMutation.getId(), input);
    }

    @SchemaMapping(typeName = "QuizMutation")
    public Quiz updateMultipleChoiceQuestion(@Argument UpdateMultipleChoiceQuestionInput input, QuizMutation quizMutation) {
        return quizService.updateMultipleChoiceQuestion(quizMutation.getId(), input);
    }

    @SchemaMapping(typeName = "QuizMutation")
    public Quiz removeQuestion(@Argument int number, QuizMutation quizMutation) {
        return quizService.removeQuestion(quizMutation.getId(), number);
    }

    @SchemaMapping(typeName = "QuizMutation")
    public Quiz switchQuestions(@Argument int firstNumber, @Argument int secondNumber, QuizMutation quizMutation) {
        return quizService.switchQuestions(quizMutation.getId(), firstNumber, secondNumber);
    }

    // TODO: add more of the nested mutations here
}
