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
                hint { text referencedMediaRecordIds }
                ... on MultipleChoiceQuestion {
                    text { text referencedMediaRecordIds }
                    answers {
                        answerText { text referencedMediaRecordIds }
                        correct
                        feedback { text referencedMediaRecordIds }
                    }
                    numberOfCorrectAnswers
                }
                ... on ClozeQuestion {
                    showBlanksList
                    clozeElements {
                        ... on ClozeBlankElement {
                            correctAnswer
                            feedback { text referencedMediaRecordIds }
                        }
                        ... on ClozeTextElement {
                            text { text referencedMediaRecordIds }
                        }
                    }
                    additionalWrongAnswers
                    allBlanks
                }
                ... on AssociationQuestion {
                    text { text referencedMediaRecordIds }
                    correctAssociations {
                        left
                        right
                        feedback { text referencedMediaRecordIds }
                    }
                    leftSide
                    rightSide
                }
                ... on ExactAnswerQuestion {
                    text { text referencedMediaRecordIds }
                    correctAnswers
                    caseSensitive
                    feedback { text referencedMediaRecordIds }
                }
                ... on NumericQuestion {
                    text { text referencedMediaRecordIds }
                    correctAnswer
                    tolerance
                    feedback { text referencedMediaRecordIds }
                }
                ... on SelfAssessmentQuestion {
                    text { text referencedMediaRecordIds }
                    solutionSuggestion { text referencedMediaRecordIds }
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
