package de.unistuttgart.iste.gits.quiz_service.api;

public class QuizFragments {

    public static final String FRAGMENT_DEFINITION = """
                        
            fragment QuestionsAllFields on Question {
                id
                number
                type
                hint { text }
                ... on MultipleChoiceQuestion {
                    text { text }
                    answers {
                        answerText { text }
                        correct
                        feedback { text }
                    }
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
            }
                        
            """;


}
