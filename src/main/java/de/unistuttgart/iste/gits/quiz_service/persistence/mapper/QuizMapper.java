package de.unistuttgart.iste.gits.quiz_service.persistence.mapper;

import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.*;
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
        return Quiz.builder()
                .setAssessmentId(entity.getAssessmentId())
                .setQuestionPoolingMode(entity.getQuestionPoolingMode())
                .setNumberOfRandomlySelectedQuestions(entity.getNumberOfRandomlySelectedQuestions())
                .setRequiredCorrectAnswers(entity.getRequiredCorrectAnswers())
                .setQuestionPool(entity.getQuestionPool().stream().map(this::questionEntityToDto).toList())
                .build();
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
        ClozeQuestion result = ClozeQuestion.builder()
                .setType(QuestionType.CLOZE)
                .setNumber(clozeQuestionEntity.getNumber())
                .setId(clozeQuestionEntity.getId())
                .setShowBlanksList(clozeQuestionEntity.isShowBlanksList())
                .setAdditionalWrongAnswers(clozeQuestionEntity.getAdditionalWrongAnswers())
                .setClozeElements(clozeQuestionEntity.getClozeElements().stream()
                        .map(this::clozeElementEntityToDto)
                        .toList())
                .build();

        if (clozeQuestionEntity.getHint() != null) {
            result.setHint(mapper.map(clozeQuestionEntity.getHint(), ResourceMarkdown.class));
        }

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
        return result;
    }

    public QuestionEntity clozeQuestionInputToEntity(UpdateClozeQuestionInput input) {
        var result = mapper.map(input, ClozeQuestionEntity.class);
        result.setType(QuestionType.CLOZE);
        return result;
    }
}
