package de.unistuttgart.iste.gits.quiz_service;

import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.*;

import java.util.*;
import java.util.stream.Stream;

public class TestData {

    public static QuizEntity.QuizEntityBuilder exampleQuizBuilder() {
        return QuizEntity.builder().assessmentId(UUID.randomUUID())
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(1)
                .requiredCorrectAnswers(1);
    }

    public static MultipleChoiceQuestionEntity createMultipleChoiceQuestion(
            int number,
            String text,
            String correctAnswerText,
            String... wrongAnswerText) {

        var builder = MultipleChoiceQuestionEntity.builder()
                .type(QuestionType.MULTIPLE_CHOICE)
                .hint("hint")
                .number(number)
                .text(text);

        var correctAnswer = MultipleChoiceAnswerEmbeddable.builder()
                .answerText(correctAnswerText)
                .feedback("feedback")
                .correct(true)
                .build();

        var wrongAnswers = Arrays.stream(wrongAnswerText)
                .map(answer -> MultipleChoiceAnswerEmbeddable.builder()
                        .answerText(answer)
                        .feedback("feedback")
                        .correct(false)
                        .build());

        builder.answers(Stream.concat(Stream.of(correctAnswer), wrongAnswers).toList());

        return builder.build();
    }

    public static ClozeQuestionEntity createClozeQuestion(int number, ClozeElementEmbeddable... clozeElements) {
        return ClozeQuestionEntity.builder()
                .type(QuestionType.CLOZE)
                .number(number)
                .showBlanksList(true)
                .additionalWrongAnswers(Arrays.asList("wrong1", "wrong2"))
                .clozeElements(Arrays.asList(clozeElements))
                .hint("hint")
                .build();
    }

    public static ClozeElementEmbeddable clozeText(String text) {
        return ClozeElementEmbeddable.builder()
                .type(ClozeElementType.TEXT)
                .text(text)
                .build();
    }

    public static ClozeElementEmbeddable clozeBlank(String correctAnswer) {
        return ClozeElementEmbeddable.builder()
                .type(ClozeElementType.BLANK)
                .correctAnswer(correctAnswer)
                .feedback("feedback")
                .build();
    }

    public static AssociationQuestionEntity createAssociationQuestion(int number, AssociationEmbeddable... associations) {
        return AssociationQuestionEntity.builder()
                .type(QuestionType.ASSOCIATION)
                .number(number)
                .text("text")
                .correctAssociations(Arrays.asList(associations))
                .hint("hint")
                .build();
    }

    public static AssociationEmbeddable association(String left, String right) {
        return association(left, right, "feedback");
    }

    public static AssociationEmbeddable association(String left, String right, String feedback) {
        return AssociationEmbeddable.builder()
                .left(left)
                .right(right)
                .feedback(feedback)
                .build();
    }

    public static ExactAnswerQuestionEntity createExactAnswerQuestion(int number, String question, String answer) {
        return ExactAnswerQuestionEntity.builder()
                .type(QuestionType.EXACT_ANSWER)
                .number(number)
                .text(question)
                .correctAnswers(Collections.singletonList(answer))
                .feedback("feedback")
                .caseSensitive(true)
                .hint("hint")
                .build();
    }

    public static NumericQuestionEntity createNumericQuestion(int number, String question, double answer) {
        return NumericQuestionEntity.builder()
                .type(QuestionType.NUMERIC)
                .number(number)
                .text(question)
                .feedback("feedback")
                .tolerance(1)
                .correctAnswer(answer)
                .hint("hint")
                .build();
    }

    public static SelfAssessmentQuestionEntity createSelfAssessmentQuestion(int number, String question, String answer) {
        return SelfAssessmentQuestionEntity.builder()
                .type(QuestionType.SELF_ASSESSMENT)
                .number(number)
                .text(question)
                .solutionSuggestion(answer)
                .hint("hint")
                .build();
    }
}
