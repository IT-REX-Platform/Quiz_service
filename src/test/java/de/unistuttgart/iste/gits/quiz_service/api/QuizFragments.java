package de.unistuttgart.iste.gits.quiz_service.api;

/**
 * This class contains GraphQL fragments for quiz queries.
 */
public class QuizFragments {

    /**
     * GraphQL fragment for all fields of a quiz.
     */
    public static final String FRAGMENT_DEFINITION = """
                        
            fragment QuestionsAllFields on Question {
                id
                number
                type
                hint
                ... on MultipleChoiceQuestion {
                    text
                    answers {
                        answerText
                        correct
                        feedback
                    }
                    numberOfCorrectAnswers
                }
                ... on ClozeQuestion {
                    showBlanksList
                    clozeElements {
                        ... on ClozeBlankElement {
                            correctAnswer
                            feedback
                        }
                        ... on ClozeTextElement {
                            text
                        }
                    }
                    additionalWrongAnswers
                    allBlanks
                }
                ... on AssociationQuestion {
                    text
                    correctAssociations {
                        left
                        right
                        feedback
                    }
                    leftSide
                    rightSide
                }
                ... on ExactAnswerQuestion {
                    text
                    correctAnswers
                    caseSensitive
                    feedback
                }
                ... on NumericQuestion {
                    text
                    correctAnswer
                    tolerance
                    feedback
                }
                ... on SelfAssessmentQuestion {
                    text
                    solutionSuggestion
                }
            }
                        
            fragment QuizAllFields on Quiz {
                assessmentId
                requiredCorrectAnswers
                questionPoolingMode
                numberOfRandomlySelectedQuestions
                questionPool {
                    ...QuestionsAllFields
                }
                selectedQuestions {
                    ...QuestionsAllFields
                }
            }
                        
            """;


}
