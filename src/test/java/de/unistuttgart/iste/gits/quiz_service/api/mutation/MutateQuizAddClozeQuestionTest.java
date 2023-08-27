package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import graphql.ErrorType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
@TablesToDelete({"cloze_question_additional_wrong_answers", "cloze_question_cloze_elements", "cloze_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizAddClozeQuestionTest {

    private static final String ADD_CLOZE_QUESTION_MUTATION = QuizFragments.FRAGMENT_DEFINITION + """
            mutation($id: UUID!, $input: CreateClozeQuestionInput!) {
                mutateQuiz(assessmentId: $id) {
                    addClozeQuestion(input: $input) {
                        ...QuizAllFields
                    }
                }
            }
            """;

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Given a quiz
     * When the "addClozeQuestion" mutation is called with a new question
     * Then the new question is added to the quiz
     */
    @Test
    @Transactional
    @Commit
    void testAddClozeQuestion(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        CreateClozeQuestionInput input = CreateClozeQuestionInput.builder()
                .setHint(new ResourceMarkdownInput("hint"))
                .setAdditionalWrongAnswers(List.of("wrong1", "wrong2"))
                .setShowBlanksList(false)
                .setClozeElements(List.of(
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.TEXT)
                                .setText(new ResourceMarkdownInput("what is the capital of France?"))
                                .build(),
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.BLANK)
                                .setCorrectAnswer("Paris")
                                .setFeedback(new ResourceMarkdownInput("feedback"))
                                .build()
                ))
                .build();


        graphQlTester.document(ADD_CLOZE_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("mutateQuiz.addClozeQuestion.questionPool[0].number")
                .entity(Integer.class)
                .isEqualTo(1)

                .path("mutateQuiz.addClozeQuestion.questionPool[0].showBlanksList")
                .entity(Boolean.class)
                .isEqualTo(false)

                .path("mutateQuiz.addClozeQuestion.questionPool[0].additionalWrongAnswers")
                .entityList(String.class)
                .isEqualTo(List.of("wrong1", "wrong2"))

                .path("mutateQuiz.addClozeQuestion.questionPool[0].clozeElements[0].text")
                .entity(ResourceMarkdown.class)
                .isEqualTo(new ResourceMarkdown("what is the capital of France?", List.of()))

                .path("mutateQuiz.addClozeQuestion.questionPool[0].clozeElements[1].correctAnswer")
                .entity(String.class)
                .isEqualTo("Paris")

                .path("mutateQuiz.addClozeQuestion.questionPool[0].clozeElements[1].feedback")
                .entity(ResourceMarkdown.class)
                .isEqualTo(new ResourceMarkdown("feedback", List.of()));

        QuestionEntity questionEntity = quizRepository.findById(quizEntity.getAssessmentId())
                .orElseThrow()
                .getQuestionPool()
                .get(0);

        assertThat(questionEntity, instanceOf(ClozeQuestionEntity.class));
        ClozeQuestionEntity clozeQuestionEntity = (ClozeQuestionEntity) questionEntity;
        assertThat(clozeQuestionEntity.getAdditionalWrongAnswers(), contains("wrong1", "wrong2"));
        assertThat(clozeQuestionEntity.getClozeElements(), hasSize(2));
        assertThat(clozeQuestionEntity.getClozeElements().get(0).getType(), is(ClozeElementType.TEXT));
        assertThat(clozeQuestionEntity.getClozeElements().get(0).getText().getText(), is("what is the capital of France?"));
        assertThat(clozeQuestionEntity.getClozeElements().get(1).getType(), is(ClozeElementType.BLANK));
        assertThat(clozeQuestionEntity.getClozeElements().get(1).getCorrectAnswer(), is("Paris"));
        assertThat(clozeQuestionEntity.getClozeElements().get(1).getFeedback().getText(), is("feedback"));

        assertThat(clozeQuestionEntity.getHint().getText(), is("hint"));
    }

    /**
     * Given a quiz
     * When the "addClozeQuestion" mutation is called with no blank
     * Then an error is returned
     */
    @Test
    void testAddClozeWithoutBlank(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        CreateClozeQuestionInput input = CreateClozeQuestionInput.builder()
                .setNumber(1)
                .setShowBlanksList(true)
                .setClozeElements(List.of(
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.TEXT)
                                .setText(new ResourceMarkdownInput("Some text"))
                                .build(),
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.TEXT)
                                .setText(new ResourceMarkdownInput("text"))
                                .build()))
                .build();

        graphQlTester.document(ADD_CLOZE_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(),
                            containsString("most contain at least one blank"));
                    assertThat(errors.get(0).getErrorType(), is(ErrorType.ValidationError));
                });
    }

    @Test
    void addClozeTextElementWithFeedback(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        CreateClozeQuestionInput input = CreateClozeQuestionInput.builder()
                .setNumber(1)
                .setShowBlanksList(true)
                .setClozeElements(List.of(
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.TEXT)
                                .setText(new ResourceMarkdownInput("Some text"))
                                .setFeedback(new ResourceMarkdownInput("feedback"))
                                .build(),
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.BLANK)
                                .setCorrectAnswer("correct")
                                .build()))
                .build();

        graphQlTester.document(ADD_CLOZE_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(),
                            containsString("text elements cannot have feedback"));
                    assertThat(errors.get(0).getErrorType(), is(ErrorType.ValidationError));
                });
    }

    @Test
    void addClozeElementWithCorrectAnswer(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        CreateClozeQuestionInput input = CreateClozeQuestionInput.builder()
                .setNumber(1)
                .setShowBlanksList(true)
                .setClozeElements(List.of(
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.TEXT)
                                .setCorrectAnswer("correct")
                                .build(),
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.BLANK)
                                .setCorrectAnswer("correct")
                                .build()))
                .build();

        graphQlTester.document(ADD_CLOZE_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(),
                            containsString("correct answer is not allowed for text elements"));
                    assertThat(errors.get(0).getErrorType(), is(ErrorType.ValidationError));
                });
    }

    @Test
    void addClozeTextElementWithoutText(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        CreateClozeQuestionInput input = CreateClozeQuestionInput.builder()
                .setNumber(1)
                .setShowBlanksList(true)
                .setClozeElements(List.of(
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.TEXT)
                                .build(),
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.BLANK)
                                .setCorrectAnswer("correct")
                                .build()))
                .build();

        graphQlTester.document(ADD_CLOZE_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(),
                            containsString("text is required for cloze text elements"));
                    assertThat(errors.get(0).getErrorType(), is(ErrorType.ValidationError));
                });
    }

    @Test
    void testAddBlankWithoutCorrectAnswer(GraphQlTester tester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        CreateClozeQuestionInput input = CreateClozeQuestionInput.builder()
                .setNumber(1)
                .setShowBlanksList(true)
                .setClozeElements(List.of(
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.BLANK)
                                .build()))
                .build();

        tester.document(ADD_CLOZE_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(),
                            containsString("correct answer is required for cloze blank elements"));
                    assertThat(errors.get(0).getErrorType(), is(ErrorType.ValidationError));
                });
    }

    @Test
    void testAddBlankWithText(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        CreateClozeQuestionInput input = CreateClozeQuestionInput.builder()
                .setNumber(1)
                .setShowBlanksList(true)
                .setClozeElements(List.of(
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.BLANK)
                                .setText(new ResourceMarkdownInput("text"))
                                .setCorrectAnswer("correct")
                                .build()))
                .build();

        graphQlTester.document(ADD_CLOZE_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(),
                            containsString("text is not allowed for cloze blank elements"));
                    assertThat(errors.get(0).getErrorType(), is(ErrorType.ValidationError));
                });
    }

}