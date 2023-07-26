package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static de.unistuttgart.iste.gits.quiz_service.TestData.createMultipleChoiceQuestion;
import static de.unistuttgart.iste.gits.quiz_service.matcher.MultipleChoiceQuestionDtoToUpdateInputMatcher.matchesInput;
import static de.unistuttgart.iste.gits.quiz_service.matcher.MultipleChoiceQuestionEntityToUpdateInputMatcher.matchesUpdateQuizInput;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@GraphQlApiTest
@TablesToDelete({"multiple_choice_question_answers", "multiple_choice_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizUpdateMultipleChoiceQuestionTest {

    @Autowired
    private QuizRepository quizRepository;

    @Test
    @Transactional
    @Commit
    void testUpdateMultipleChoiceQuestion(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of(
                        createMultipleChoiceQuestion(1, "what is the capital of Germany?", "Berlin", "Paris")))
                .build();
        quizEntity = quizRepository.save(quizEntity);

        UpdateMultipleChoiceQuestionInput input = UpdateMultipleChoiceQuestionInput.builder()
                .setId(quizEntity.getQuestionPool().get(0).getId())
                .setText("what is the capital of France?")
                .setAnswers(List.of(
                        MultipleChoiceAnswerInput.builder()
                                .setText("Paris")
                                .setCorrect(true)
                                .build(),
                        MultipleChoiceAnswerInput.builder()
                                .setText("Madrid")
                                .setCorrect(false)
                                .build()))
                .build();

        String query = """
                mutation($id: UUID!, $input: UpdateMultipleChoiceQuestionInput!) {
                    mutateQuiz(assessmentId: $id) {
                        updateMultipleChoiceQuestion(input: $input) {
                            questionPool {
                                id
                                number
                                type
                                hint
                                ... on MultipleChoiceQuestion {
                                    text
                                    answers {
                                        text
                                        correct
                                        feedback
                                    }
                                }
                            }
                        }
                    }
                }
                """;

        List<MultipleChoiceQuestion> questions = graphQlTester.document(query)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("mutateQuiz.updateMultipleChoiceQuestion.questionPool")
                .entityList(MultipleChoiceQuestion.class)
                .get();

        assertThat(questions, hasSize(1));
        assertThat(questions.get(0), matchesInput(input));

        QuizEntity updatedQuiz = quizRepository.findById(quizEntity.getAssessmentId()).orElseThrow();
        assertThat(updatedQuiz.getQuestionPool(), hasSize(1));
        assertThat(updatedQuiz.getQuestionPool().get(0), matchesUpdateQuizInput(input));

    }
}
