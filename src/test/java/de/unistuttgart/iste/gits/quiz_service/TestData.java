package de.unistuttgart.iste.gits.quiz_service;

import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.*;

import java.util.*;
import java.util.stream.Stream;

public class TestData {

    public static QuizEntity.QuizEntityBuilder exampleQuizBuilder() {
        return QuizEntity.builder()
                .courseId(UUID.randomUUID())
                .assessmentId(UUID.randomUUID())
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(1)
                .requiredCorrectAnswers(1);
    }

    public static MultipleChoiceQuestionEntity createMultipleChoiceQuestion(
            final int number,
            final String text,
            final String correctAnswerText,
            final String... wrongAnswerText) {

        final var builder = MultipleChoiceQuestionEntity.builder()
                .type(QuestionType.MULTIPLE_CHOICE)
                .hint("hint")
                .number(number)
                .text(text);

        final var correctAnswer = MultipleChoiceAnswerEmbeddable.builder()
                .answerText(correctAnswerText)
                .feedback("feedback")
                .correct(true)
                .build();

        final var wrongAnswers = Arrays.stream(wrongAnswerText)
                .map(answer -> MultipleChoiceAnswerEmbeddable.builder()
                        .answerText(answer)
                        .feedback("feedback")
                        .correct(false)
                        .build());

        builder.answers(Stream.concat(Stream.of(correctAnswer), wrongAnswers).toList());

        return builder.build();
    }

    public static ClozeQuestionEntity createClozeQuestion(final int number, final ClozeElementEmbeddable... clozeElements) {
        return ClozeQuestionEntity.builder()
                .type(QuestionType.CLOZE)
                .number(number)
                .showBlanksList(true)
                .additionalWrongAnswers(Arrays.asList("wrong1", "wrong2"))
                .clozeElements(Arrays.asList(clozeElements))
                .hint("hint")
                .build();
    }

    public static ClozeElementEmbeddable clozeText(final String text) {
        return ClozeElementEmbeddable.builder()
                .type(ClozeElementType.TEXT)
                .text(text)
                .build();
    }

    public static ClozeElementEmbeddable clozeBlank(final String correctAnswer) {
        return ClozeElementEmbeddable.builder()
                .type(ClozeElementType.BLANK)
                .correctAnswer(correctAnswer)
                .feedback("feedback")
                .build();
    }

    public static AssociationQuestionEntity createAssociationQuestion(final int number, final AssociationEmbeddable... associations) {
        return AssociationQuestionEntity.builder()
                .type(QuestionType.ASSOCIATION)
                .number(number)
                .text("text")
                .correctAssociations(Arrays.asList(associations))
                .hint("hint")
                .build();
    }

    public static AssociationEmbeddable association(final String left, final String right) {
        return association(left, right, "feedback");
    }

    public static AssociationEmbeddable association(final String left, final String right, final String feedback) {
        return AssociationEmbeddable.builder()
                .left(left)
                .right(right)
                .feedback(feedback)
                .build();
    }

    public static ExactAnswerQuestionEntity createExactAnswerQuestion(final int number, final String question, final String answer) {
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

    public static NumericQuestionEntity createNumericQuestion(final int number, final String question, final double answer) {
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

    public static SelfAssessmentQuestionEntity createSelfAssessmentQuestion(final int number, final String question, final String answer) {
        return SelfAssessmentQuestionEntity.builder()
                .type(QuestionType.SELF_ASSESSMENT)
                .number(number)
                .text(question)
                .solutionSuggestion(answer)
                .hint("hint")
                .build();
    }

    /**
     * creates some dummy multiple choice questions
     *
     * @return List of 2 Multiple Choice Question (database) Entities
     */
    public static List<QuestionEntity> createDummyQuestions() {
        final List<QuestionEntity> questions = new ArrayList<>();
        final MultipleChoiceAnswerEmbeddable wrongAnswer = MultipleChoiceAnswerEmbeddable.builder()
                .answerText("Pick me! Pick Me!")
                .correct(false)
                .feedback("Fell for it")
                .build();
        final MultipleChoiceAnswerEmbeddable correctAnswer = MultipleChoiceAnswerEmbeddable.builder()
                .answerText("No me!")
                .correct(true)
                .feedback("Well done!")
                .build();
        final MultipleChoiceQuestionEntity questionEntity = MultipleChoiceQuestionEntity.builder()
                .id(UUID.randomUUID())
                .number(0)
                .type(QuestionType.MULTIPLE_CHOICE)
                .text("This is a question")
                .answers(List.of(wrongAnswer, correctAnswer))
                .hint("Wink Wink")
                .build();
        final MultipleChoiceQuestionEntity questionEntity2 = MultipleChoiceQuestionEntity.builder()
                .id(UUID.randomUUID())
                .number(0)
                .type(QuestionType.MULTIPLE_CHOICE)
                .text("This is a question")
                .answers(List.of(wrongAnswer, correctAnswer))
                .hint("Wink Wink")
                .build();

        questions.add(questionEntity);
        questions.add(questionEntity2);

        return questions;
    }
}
