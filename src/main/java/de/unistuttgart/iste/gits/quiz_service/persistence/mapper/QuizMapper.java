package de.unistuttgart.iste.gits.quiz_service.persistence.mapper;

import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class QuizMapper {

    private final ModelMapper mapper;

    /**
     * Maps a quiz entity to a quiz dto but does not set {@link Quiz#getSelectedQuestions()}.
     */
    public Quiz entityToDto(final QuizEntity entity) {
        // manual mapping necessary because of QuestionInterface
        // which cannot automatically be mapped by model mapper
        final Quiz result = Quiz.builder()
                .setAssessmentId(entity.getAssessmentId())
                .setQuestionPoolingMode(entity.getQuestionPoolingMode())
                .setNumberOfRandomlySelectedQuestions(entity.getNumberOfRandomlySelectedQuestions())
                .setRequiredCorrectAnswers(entity.getRequiredCorrectAnswers())
                .setQuestionPool(entity.getQuestionPool().stream().map(this::questionEntityToDto).toList())
                .build();
        return selectQuestionsInQuiz(result);
    }

    /**
     * Selects the questions for a quiz based on the question pooling mode.
     * <p>
     * If the question pooling mode is {@link QuestionPoolingMode#ORDERED}, all questions are selected
     * and the order is preserved.
     * If the question pooling mode is {@link QuestionPoolingMode#RANDOM}, the questions are shuffled
     * and the number of questions is limited to {@link Quiz#getNumberOfRandomlySelectedQuestions()}.
     *
     * @param quiz the quiz
     * @return the quiz with the selected questions
     */
    private Quiz selectQuestionsInQuiz(final Quiz quiz) {
        if (quiz.getQuestionPoolingMode() == QuestionPoolingMode.ORDERED) {
            quiz.setSelectedQuestions(quiz.getQuestionPool());
            return quiz;
        }

        int limit = quiz.getQuestionPool().size();
        if (quiz.getNumberOfRandomlySelectedQuestions() != null) {
            limit = Math.min(limit, quiz.getNumberOfRandomlySelectedQuestions());
        }

        final List<Question> pool = new ArrayList<>(quiz.getQuestionPool());
        Collections.shuffle(pool);
        quiz.setSelectedQuestions(pool.subList(0, limit));

        return quiz;
    }

    private Question questionEntityToDto(final QuestionEntity questionEntity) {
        if (questionEntity instanceof final MultipleChoiceQuestionEntity multipleChoiceQuestionEntity) {
            return multipleChoiceQuestionEntityToDto(multipleChoiceQuestionEntity);
        }
        if (questionEntity instanceof final AssociationQuestionEntity associationQuestionEntity) {
            return associationQuestionEntityToDto(associationQuestionEntity);
        }
        if (questionEntity instanceof final SelfAssessmentQuestionEntity selfAssessmentQuestionEntity) {
            return selfAssessmentQuestionEntityToDto(selfAssessmentQuestionEntity);
        }
        if (questionEntity instanceof final ClozeQuestionEntity clozeQuestionEntity) {
            return clozeQuestionEntityToDto(clozeQuestionEntity);
        }
        if (questionEntity instanceof final NumericQuestionEntity numericQuestionEntity) {
            return numericQuestionEntityToDto(numericQuestionEntity);
        }
        if (questionEntity instanceof final ExactAnswerQuestionEntity exactAnswerQuestionEntity) {
            return exactAnswerQuestionEntityToDto(exactAnswerQuestionEntity);
        }

        // add other question types here
        throw new IllegalArgumentException("Unknown question type: " + questionEntity.getType());
    }

    public MultipleChoiceQuestion multipleChoiceQuestionEntityToDto(final MultipleChoiceQuestionEntity multipleChoiceQuestionEntity) {
        return mapper.map(multipleChoiceQuestionEntity, MultipleChoiceQuestion.class);
    }

    public ClozeQuestion clozeQuestionEntityToDto(final ClozeQuestionEntity clozeQuestionEntity) {
        // manual mapping necessary because of ClozeElement interface
        // which cannot automatically be mapped by model mapper
        final ClozeQuestion result = ClozeQuestion.builder()
                .setType(QuestionType.CLOZE)
                .setNumber(clozeQuestionEntity.getNumber())
                .setId(clozeQuestionEntity.getId())
                .setShowBlanksList(clozeQuestionEntity.isShowBlanksList())
                .setAdditionalWrongAnswers(clozeQuestionEntity.getAdditionalWrongAnswers())
                .setClozeElements(clozeQuestionEntity.getClozeElements().stream()
                        .map(this::clozeElementEntityToDto)
                        .toList())
                .setHint(clozeQuestionEntity.getHint())
                .build();

        final List<String> allBlanks = new ArrayList<>(result.getAdditionalWrongAnswers());
        result.getClozeElements().stream()
                // filter out cloze text elements
                .filter(ClozeBlankElement.class::isInstance)
                .map(ClozeBlankElement.class::cast)
                // get correct answers of blank elements
                .map(ClozeBlankElement::getCorrectAnswer)
                .forEach(allBlanks::add);

        Collections.shuffle(allBlanks);
        result.setAllBlanks(allBlanks);

        return result;
    }

    private ClozeElement clozeElementEntityToDto(final ClozeElementEmbeddable clozeElementEmbeddable) {
        if (clozeElementEmbeddable.getType() == ClozeElementType.TEXT) {
            return mapper.map(clozeElementEmbeddable, ClozeTextElement.class);
        }
        return mapper.map(clozeElementEmbeddable, ClozeBlankElement.class);
    }

    private AssociationQuestion associationQuestionEntityToDto(final AssociationQuestionEntity associationQuestionEntity) {
        final AssociationQuestion result = mapper.map(associationQuestionEntity, AssociationQuestion.class);

        final List<String> leftSide = new ArrayList<>(result.getCorrectAssociations().size());
        final List<String> rightSide = new ArrayList<>(result.getCorrectAssociations().size());

        for (final SingleAssociation association : result.getCorrectAssociations()) {
            leftSide.add(association.getLeft());
            rightSide.add(association.getRight());
        }

        Collections.shuffle(leftSide);
        Collections.shuffle(rightSide);

        result.setLeftSide(leftSide);
        result.setRightSide(rightSide);

        return result;
    }

    private NumericQuestion numericQuestionEntityToDto(final NumericQuestionEntity numericQuestionEntity) {
        return mapper.map(numericQuestionEntity, NumericQuestion.class);
    }

    private ExactAnswerQuestion exactAnswerQuestionEntityToDto(final ExactAnswerQuestionEntity exactAnswerQuestionEntity) {
        return mapper.map(exactAnswerQuestionEntity, ExactAnswerQuestion.class);
    }

    public SelfAssessmentQuestion selfAssessmentQuestionEntityToDto(final SelfAssessmentQuestionEntity selfAssessmentQuestionEntity) {
        return mapper.map(selfAssessmentQuestionEntity, SelfAssessmentQuestion.class);
    }

    public QuizEntity createQuizInputToEntity(final CreateQuizInput createQuizInput) {
        return mapper.map(createQuizInput, QuizEntity.class);
    }

    public QuestionEntity multipleChoiceQuestionInputToEntity(final CreateMultipleChoiceQuestionInput input) {
        final MultipleChoiceQuestionEntity result = mapper.map(input, MultipleChoiceQuestionEntity.class);
        result.setType(QuestionType.MULTIPLE_CHOICE);
        return result;
    }

    public QuestionEntity multipleChoiceQuestionInputToEntity(final UpdateMultipleChoiceQuestionInput input) {
        final MultipleChoiceQuestionEntity result = mapper.map(input, MultipleChoiceQuestionEntity.class);
        result.setType(QuestionType.MULTIPLE_CHOICE);
        return result;
    }

    public QuestionEntity clozeQuestionInputToEntity(final CreateClozeQuestionInput input) {
        final var result = mapper.map(input, ClozeQuestionEntity.class);
        result.setType(QuestionType.CLOZE);
        setPositionNumbersInClozeElements(result.getClozeElements());
        return result;
    }

    public QuestionEntity clozeQuestionInputToEntity(final UpdateClozeQuestionInput input) {
        final var result = mapper.map(input, ClozeQuestionEntity.class);
        result.setType(QuestionType.CLOZE);
        setPositionNumbersInClozeElements(result.getClozeElements());
        return result;
    }

    private void setPositionNumbersInClozeElements(final List<ClozeElementEmbeddable> clozeElements) {
        int position = 1;
        for (final ClozeElementEmbeddable clozeElement : clozeElements) {
            clozeElement.setPosition(position++);
        }
    }

    public QuestionEntity associationQuestionInputToEntity(final CreateAssociationQuestionInput input) {
        final var result = mapper.map(input, AssociationQuestionEntity.class);
        result.setType(QuestionType.ASSOCIATION);
        return result;
    }

    public QuestionEntity associationQuestionInputToEntity(final UpdateAssociationQuestionInput input) {
        final var result = mapper.map(input, AssociationQuestionEntity.class);
        result.setType(QuestionType.ASSOCIATION);
        return result;
    }

    public QuestionEntity exactAnswerQuestionInputToEntity(final CreateExactAnswerQuestionInput input) {
        final var result = mapper.map(input, ExactAnswerQuestionEntity.class);
        result.setType(QuestionType.EXACT_ANSWER);
        return result;
    }

    public QuestionEntity exactAnswerQuestionInputToEntity(final UpdateExactAnswerQuestionInput input) {
        final var result = mapper.map(input, ExactAnswerQuestionEntity.class);
        result.setType(QuestionType.EXACT_ANSWER);
        return result;
    }

    public QuestionEntity numericQuestionInputToEntity(final CreateNumericQuestionInput input) {
        final var result = mapper.map(input, NumericQuestionEntity.class);
        result.setType(QuestionType.NUMERIC);
        return result;
    }

    public QuestionEntity numericQuestionInputToEntity(final UpdateNumericQuestionInput input) {
        final var result = mapper.map(input, NumericQuestionEntity.class);
        result.setType(QuestionType.NUMERIC);
        return result;
    }

    public QuestionEntity selfAssessmentQuestionInputToEntity(final CreateSelfAssessmentQuestionInput input) {
        final var result = mapper.map(input, SelfAssessmentQuestionEntity.class);
        result.setType(QuestionType.SELF_ASSESSMENT);
        return result;
    }

    public QuestionEntity selfAssessmentQuestionInputToEntity(final UpdateSelfAssessmentQuestionInput input) {
        final var result = mapper.map(input, SelfAssessmentQuestionEntity.class);
        result.setType(QuestionType.SELF_ASSESSMENT);
        return result;
    }
}
