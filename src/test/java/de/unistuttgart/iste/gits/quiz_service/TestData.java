package de.unistuttgart.iste.gits.quiz_service;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEmbeddable;
import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEntity;
import de.unistuttgart.iste.gits.generated.dto.QuestionPoolingMode;
import de.unistuttgart.iste.gits.generated.dto.QuestionType;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.*;

import java.util.Arrays;
import java.util.UUID;
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
                .number(number)
                .text(new ResourceMarkdownEmbeddable(text));

        var correctAnswer = MultipleChoiceAnswerEmbeddable.builder()
                .answerText(new ResourceMarkdownEntity(correctAnswerText))
                .correct(true)
                .build();

        var wrongAnswers = Arrays.stream(wrongAnswerText)
                .map(answer -> MultipleChoiceAnswerEmbeddable.builder()
                        .answerText(new ResourceMarkdownEntity(answer))
                        .correct(false)
                        .build());

        builder.answers(Stream.concat(Stream.of(correctAnswer), wrongAnswers).toList());

        return builder.build();
    }
}
