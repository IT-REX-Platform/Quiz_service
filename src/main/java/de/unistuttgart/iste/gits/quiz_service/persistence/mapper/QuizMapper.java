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
    public Quiz entityToDto(QuizEntity entity) {
        // manual mapping necessary because of QuestionInterface
        // which cannot automatically be mapped by model mapper
        Quiz result = Quiz.builder()
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
    private Quiz selectQuestionsInQuiz(Quiz quiz) {
        if (quiz.getQuestionPoolingMode() == QuestionPoolingMode.ORDERED) {
            quiz.setSelectedQuestions(quiz.getQuestionPool());
            return quiz;
        }

        int limit = quiz.getQuestionPool().size();
        if (quiz.getNumberOfRandomlySelectedQuestions() != null) {
            limit = Math.min(limit, quiz.getNumberOfRandomlySelectedQuestions());
        }

        List<Question> pool = new ArrayList<>(quiz.getQuestionPool());
        Collections.shuffle(pool);
        quiz.setSelectedQuestions(pool.subList(0, limit));

        return quiz;
    }

    private Question questionEntityToDto(QuestionEntity questionEntity) {
        if (questionEntity instanceof MultipleChoiceQuestionEntity multipleChoiceQuestionEntity) {
            return multipleChoiceQuestionEntityToDto(multipleChoiceQuestionEntity);
        }
        if (questionEntity instanceof AssociationQuestionEntity associationQuestionEntity) {
            return associationQuestionEntityToDto(associationQuestionEntity);
        }
        if (questionEntity instanceof SelfAssessmentQuestionEntity selfAssessmentQuestionEntity) {
            return selfAssessmentQuestionEntityToDto(selfAssessmentQuestionEntity);
        }
        if (questionEntity instanceof ClozeQuestionEntity clozeQuestionEntity) {
            return clozeQuestionEntityToDto(clozeQuestionEntity);
        }
        if (questionEntity instanceof NumericQuestionEntity numericQuestionEntity) {
            return numericQuestionEntityToDto(numericQuestionEntity);
        }
        if (questionEntity instanceof ExactAnswerQuestionEntity exactAnswerQuestionEntity) {
            return exactAnswerQuestionEntityToDto(exactAnswerQuestionEntity);
        }

        // add other question types here
        throw new IllegalArgumentException("Unknown question type: " + questionEntity.getType());
    }

    public MultipleChoiceQuestion multipleChoiceQuestionEntityToDto(MultipleChoiceQuestionEntity multipleChoiceQuestionEntity) {
        return mapper.map(multipleChoiceQuestionEntity, MultipleChoiceQuestion.class);
    }

    public ClozeQuestion clozeQuestionEntityToDto(ClozeQuestionEntity clozeQuestionEntity) {
        // manual mapping necessary because of ClozeElement interface
        // which cannot automatically be mapped by model mapper
        ClozeQuestion result = ClozeQuestion.builder()
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

        List<String> allBlanks = new ArrayList<>(result.getAdditionalWrongAnswers().size() + result.getClozeElements().size());
        allBlanks.addAll(result.getAdditionalWrongAnswers());
        result.getClozeElements().stream()
                .filter(ClozeBlankElement.class::isInstance)
                .map(ClozeBlankElement.class::cast)
                .map(ClozeBlankElement::getCorrectAnswer)
                .forEach(allBlanks::add);

        Collections.shuffle(allBlanks);
        result.setAllBlanks(allBlanks);

        return result;
    }

    private ClozeElement clozeElementEntityToDto(ClozeElementEmbeddable clozeElementEmbeddable) {
        if (clozeElementEmbeddable.getType() == ClozeElementType.TEXT) {
            return mapper.map(clozeElementEmbeddable, ClozeTextElement.class);
        }
        return mapper.map(clozeElementEmbeddable, ClozeBlankElement.class);
    }

    private AssociationQuestion associationQuestionEntityToDto(AssociationQuestionEntity associationQuestionEntity) {
        AssociationQuestion result = mapper.map(associationQuestionEntity, AssociationQuestion.class);

        List<String> leftSide = new ArrayList<>(result.getCorrectAssociations().size());
        List<String> rightSide = new ArrayList<>(result.getCorrectAssociations().size());

        for (SingleAssociation association : result.getCorrectAssociations()) {
            leftSide.add(association.getLeft());
            rightSide.add(association.getRight());
        }

        Collections.shuffle(leftSide);
        Collections.shuffle(rightSide);

        result.setLeftSide(leftSide);
        result.setRightSide(rightSide);

        return result;
    }

    private NumericQuestion numericQuestionEntityToDto(NumericQuestionEntity numericQuestionEntity) {
        return mapper.map(numericQuestionEntity, NumericQuestion.class);
    }

    private ExactAnswerQuestion exactAnswerQuestionEntityToDto(ExactAnswerQuestionEntity exactAnswerQuestionEntity) {
        return mapper.map(exactAnswerQuestionEntity, ExactAnswerQuestion.class);
    }

    public SelfAssessmentQuestion selfAssessmentQuestionEntityToDto(SelfAssessmentQuestionEntity selfAssessmentQuestionEntity) {
        return mapper.map(selfAssessmentQuestionEntity, SelfAssessmentQuestion.class);
    }

    public QuizEntity createQuizInputToEntity(CreateQuizInput createQuizInput) {
        return mapper.map(createQuizInput, QuizEntity.class);
    }

    public QuestionEntity multipleChoiceQuestionInputToEntity(CreateMultipleChoiceQuestionInput input) {
        MultipleChoiceQuestionEntity result = mapper.map(input, MultipleChoiceQuestionEntity.class);
        result.setType(QuestionType.MULTIPLE_CHOICE);
        return result;
    }

    public QuestionEntity multipleChoiceQuestionInputToEntity(UpdateMultipleChoiceQuestionInput input) {
        MultipleChoiceQuestionEntity result = mapper.map(input, MultipleChoiceQuestionEntity.class);
        result.setType(QuestionType.MULTIPLE_CHOICE);
        return result;
    }

    public QuestionEntity clozeQuestionInputToEntity(CreateClozeQuestionInput input) {
        var result = mapper.map(input, ClozeQuestionEntity.class);
        result.setType(QuestionType.CLOZE);
        setPositionNumbersInClozeElements(result.getClozeElements());
        return result;
    }

    public QuestionEntity clozeQuestionInputToEntity(UpdateClozeQuestionInput input) {
        var result = mapper.map(input, ClozeQuestionEntity.class);
        result.setType(QuestionType.CLOZE);
        setPositionNumbersInClozeElements(result.getClozeElements());
        return result;
    }

    private void setPositionNumbersInClozeElements(List<ClozeElementEmbeddable> clozeElements) {
        int position = 1;
        for (ClozeElementEmbeddable clozeElement : clozeElements) {
            clozeElement.setPosition(position++);
        }
    }

    public QuestionEntity associationQuestionInputToEntity(CreateAssociationQuestionInput input) {
        var result = mapper.map(input, AssociationQuestionEntity.class);
        result.setType(QuestionType.ASSOCIATION);
        return result;
    }

    public QuestionEntity associationQuestionInputToEntity(UpdateAssociationQuestionInput input) {
        var result = mapper.map(input, AssociationQuestionEntity.class);
        result.setType(QuestionType.ASSOCIATION);
        return result;
    }

    public QuestionEntity exactAnswerQuestionInputToEntity(CreateExactAnswerQuestionInput input) {
        var result = mapper.map(input, ExactAnswerQuestionEntity.class);
        result.setType(QuestionType.EXACT_ANSWER);
        return result;
    }

    public QuestionEntity exactAnswerQuestionInputToEntity(UpdateExactAnswerQuestionInput input) {
        var result = mapper.map(input, ExactAnswerQuestionEntity.class);
        result.setType(QuestionType.EXACT_ANSWER);
        return result;
    }

    public QuestionEntity numericQuestionInputToEntity(CreateNumericQuestionInput input) {
        var result = mapper.map(input, NumericQuestionEntity.class);
        result.setType(QuestionType.NUMERIC);
        return result;
    }

    public QuestionEntity numericQuestionInputToEntity(UpdateNumericQuestionInput input) {
        var result = mapper.map(input, NumericQuestionEntity.class);
        result.setType(QuestionType.NUMERIC);
        return result;
    }

    public QuestionEntity selfAssessmentQuestionInputToEntity(CreateSelfAssessmentQuestionInput input) {
        var result = mapper.map(input, SelfAssessmentQuestionEntity.class);
        result.setType(QuestionType.SELF_ASSESSMENT);
        return result;
    }

    public QuestionEntity selfAssessmentQuestionInputToEntity(UpdateSelfAssessmentQuestionInput input) {
        var result = mapper.map(input, SelfAssessmentQuestionEntity.class);
        result.setType(QuestionType.SELF_ASSESSMENT);
        return result;
    }
}
