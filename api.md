# Quiz Service API

<details>
  <summary><strong>Table of Contents</strong></summary>

* [Query](#query)
* [Mutation](#mutation)
* [Objects](#objects)
    * [AssociationQuestion](#associationquestion)
    * [ClozeBlankElement](#clozeblankelement)
    * [ClozeQuestion](#clozequestion)
    * [ClozeTextElement](#clozetextelement)
    * [ExactAnswerQuestion](#exactanswerquestion)
    * [MultipleChoiceAnswer](#multiplechoiceanswer)
    * [MultipleChoiceQuestion](#multiplechoicequestion)
    * [NumericQuestion](#numericquestion)
    * [PaginationInfo](#paginationinfo)
    * [Quiz](#quiz)
    * [QuizMutation](#quizmutation)
    * [ResourceMarkdown](#resourcemarkdown)
    * [SelfAssessmentQuestion](#selfassessmentquestion)
    * [SingleAssociation](#singleassociation)
* [Inputs](#inputs)
    * [AssociationInput](#associationinput)
    * [ClozeElementInput](#clozeelementinput)
    * [CreateAssociationQuestionInput](#createassociationquestioninput)
    * [CreateClozeQuestionInput](#createclozequestioninput)
    * [CreateExactAnswerQuestionInput](#createexactanswerquestioninput)
    * [CreateMultipleChoiceQuestionInput](#createmultiplechoicequestioninput)
    * [CreateNumericQuestionInput](#createnumericquestioninput)
    * [CreateQuizInput](#createquizinput)
    * [CreateSelfAssessmentQuestionInput](#createselfassessmentquestioninput)
    * [DateTimeFilter](#datetimefilter)
    * [IntFilter](#intfilter)
    * [MultipleChoiceAnswerInput](#multiplechoiceanswerinput)
    * [Pagination](#pagination)
    * [QuestionCompletedInput](#questioncompletedinput)
    * [QuizCompletedInput](#quizcompletedinput)
    * [ResourceMarkdownInput](#resourcemarkdowninput)
    * [StringFilter](#stringfilter)
    * [UpdateAssociationQuestionInput](#updateassociationquestioninput)
    * [UpdateClozeQuestionInput](#updateclozequestioninput)
    * [UpdateExactAnswerQuestionInput](#updateexactanswerquestioninput)
    * [UpdateMultipleChoiceQuestionInput](#updatemultiplechoicequestioninput)
    * [UpdateNumericQuestionInput](#updatenumericquestioninput)
    * [UpdateSelfAssessmentQuestionInput](#updateselfassessmentquestioninput)
* [Enums](#enums)
    * [ClozeElementType](#clozeelementtype)
    * [QuestionPoolingMode](#questionpoolingmode)
    * [QuestionType](#questiontype)
    * [SortDirection](#sortdirection)
* [Scalars](#scalars)
    * [Boolean](#boolean)
    * [Date](#date)
    * [DateTime](#datetime)
    * [Float](#float)
    * [Int](#int)
    * [JSON](#json)
    * [LocalTime](#localtime)
    * [String](#string)
    * [Time](#time)
    * [UUID](#uuid)
    * [Url](#url)
* [Interfaces](#interfaces)
    * [Question](#question)
* [Unions](#unions)
    * [ClozeElement](#clozeelement)

</details>

## Query

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>findQuizzesByAssessmentIds</strong></td>
<td valign="top">[<a href="#quiz">Quiz</a>]!</td>
<td>


Get quiz by assessment ID.
If any of the assessment IDs are not found, the corresponding quiz will be null.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">assessmentIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
</tbody>
</table>

## Mutation

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>createQuiz</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Create a quiz.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">assessmentId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createquizinput">CreateQuizInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>mutateQuiz</strong></td>
<td valign="top"><a href="#quizmutation">QuizMutation</a>!</td>
<td>


Modify a quiz.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">assessmentId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>deleteQuiz</strong> ⚠️</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


Delete a quiz.

<p>⚠️ <strong>DEPRECATED</strong></p>
<blockquote>

Only for development, will be removed in production. Use deleteAssessment in contents service instead.

</blockquote>
</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">assessmentId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>logQuizCompleted</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Log that a multiple choice quiz is completed.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#quizcompletedinput">QuizCompletedInput</a>!</td>
<td></td>
</tr>
</tbody>
</table>

## Objects

### AssociationQuestion

Association question, i.e., a question where the user has to assign the correct right side to each left side.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text to display above the association question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correctAssociations</strong></td>
<td valign="top">[<a href="#singleassociation">SingleAssociation</a>!]!</td>
<td>


List of correct associations.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>leftSide</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>


Computed list of all the left sides of the associations, shuffled.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>rightSide</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>


Computed list of all the right sides of the associations, shuffled.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


Unique identifier of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


Number of the question, i.e., the position of the question in the list of questions.
Only relevant if questionPoolingMode is ORDERED.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>type</strong></td>
<td valign="top"><a href="#questiontype">QuestionType</a>!</td>
<td>


Type of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### ClozeBlankElement

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>correctAnswer</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>


The correct answer for the blank.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for the blank when the user selects a wrong answer, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### ClozeQuestion

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>clozeElements</strong></td>
<td valign="top">[<a href="#clozeelement">ClozeElement</a>!]!</td>
<td>


The elements of the cloze question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>additionalWrongAnswers</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>


Addtional wrong answers for the blanks.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>allBlanks</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>


All selectable answers for the blanks (computed). This contains the correct answers as well as wrong answers.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>showBlanksList</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


Whether the blanks must be answered in free text or by selecting the correct answer from a list.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


Unique identifier of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


Number of the question, i.e., the position of the question in the list of questions.
Only relevant if questionPoolingMode is ORDERED.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>type</strong></td>
<td valign="top"><a href="#questiontype">QuestionType</a>!</td>
<td>


Type of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### ClozeTextElement

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the element, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### ExactAnswerQuestion

A question with a clear, correct answer that can be automatically checked.
Differs from self-assessment questions in that the user has to enter one of the correct answers and
the answer is checked automatically.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correctAnswers</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>


A list of possible correct answers. The user has to enter one of these answers.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>caseSensitive</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


If the answer is case sensitive. If true, the answer is checked case sensitive.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for the question when the user enters a wrong answer, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


Unique identifier of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


Number of the question, i.e., the position of the question in the list of questions.
Only relevant if questionPoolingMode is ORDERED.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>type</strong></td>
<td valign="top"><a href="#questiontype">QuestionType</a>!</td>
<td>


Type of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### MultipleChoiceAnswer

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>answerText</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the answer, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correct</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


Whether the answer is correct or not.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for when the user selects this answer, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### MultipleChoiceQuestion

Multiple choice question, i.e., a question with multiple answers of which the user has to select the correct ones.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>answers</strong></td>
<td valign="top">[<a href="#multiplechoiceanswer">MultipleChoiceAnswer</a>!]!</td>
<td>


List of answers.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>numberOfCorrectAnswers</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


How many answers the user has to select. This is computed from the list of answers.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


Unique identifier of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


Number of the question, i.e., the position of the question in the list of questions.
Only relevant if questionPoolingMode is ORDERED.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>type</strong></td>
<td valign="top"><a href="#questiontype">QuestionType</a>!</td>
<td>


Type of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### NumericQuestion

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correctAnswer</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>


The correct answer to the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>tolerance</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>


The tolerance for the correct answer. The user's answer is correct if it is within the tolerance of the correct answer.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for the question when the user enters a wrong answer, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


Unique identifier of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


Number of the question, i.e., the position of the question in the list of questions.
Only relevant if questionPoolingMode is ORDERED.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>type</strong></td>
<td valign="top"><a href="#questiontype">QuestionType</a>!</td>
<td>


Type of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### PaginationInfo

Return type for information about paginated results.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>page</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


The current page number.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>size</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


The number of elements per page.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>totalElements</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


The total number of elements across all pages.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>totalPages</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


The total number of pages.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hasNext</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


Whether there is a next page.

</td>
</tr>
</tbody>
</table>

### Quiz

A quiz is a set of questions that the user has to answer correctly to pass the quiz.
Questions can be of different types, e.g., multiple choice, clozes, or open questions.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>assessmentId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


Identifier of the quiz, same as the identifier of the assessment.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>questionPool</strong></td>
<td valign="top">[<a href="#question">Question</a>!]!</td>
<td>


List of questions.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>requiredCorrectAnswers</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


Threshold of the quiz, i.e., how many questions the user has to answer correctly to pass the quiz.
If this number is greater than the number of questions, the behavior is the same
as if it was equal to the number of questions.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>questionPoolingMode</strong></td>
<td valign="top"><a href="#questionpoolingmode">QuestionPoolingMode</a>!</td>
<td>


Question pooling mode of the quiz.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>numberOfRandomlySelectedQuestions</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

    Number of questions that are randomly selected from the list of questions.
    Will only be considered if questionPoolingMode is RANDOM.

    If this is greater than the number of questions, the behavior is the same
    as if it was equal to the number of questions.

    If this is null or not set, the behavior is the same as if it was equal to the number of questions.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>selectedQuestions</strong></td>
<td valign="top">[<a href="#question">Question</a>!]!</td>
<td>


The selected questions of the question pool.
This is identical to the list of questions if questionPoolingMode is ORDERED.
This will be different each time it is queried if questionPoolingMode is RANDOM.

</td>
</tr>
</tbody>
</table>

### QuizMutation

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>assessmentId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


Id of the quiz to modify.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>addMultipleChoiceQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Add a multiple choice question to the quiz questions, at the end of the list.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createmultiplechoicequestioninput">CreateMultipleChoiceQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>updateMultipleChoiceQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Update a multiple choice question in the quiz questions.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#updatemultiplechoicequestioninput">UpdateMultipleChoiceQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>addClozeQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Add a cloze question to the quiz questions, at the end of the list.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createclozequestioninput">CreateClozeQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>updateClozeQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Update a cloze question in the quiz questions.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#updateclozequestioninput">UpdateClozeQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>addAssociationQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Add an association question to the quiz questions, at the end of the list.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createassociationquestioninput">CreateAssociationQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>updateAssociationQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Update an association question in the quiz questions.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#updateassociationquestioninput">UpdateAssociationQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>addExactAnswerQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Add an free text question with exact answer to the quiz questions, at the end of the list.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createexactanswerquestioninput">CreateExactAnswerQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>updateExactAnswerQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Update an free text question with exact answer in the quiz questions.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#updateexactanswerquestioninput">UpdateExactAnswerQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>addNumericQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Add a numeric question to the quiz questions, at the end of the list.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createnumericquestioninput">CreateNumericQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>updateNumericQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Update a numeric question in the quiz questions.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#updatenumericquestioninput">UpdateNumericQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>addSelfAssessmentQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Add a self assessment question to the quiz questions, at the end of the list.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createselfassessmentquestioninput">CreateSelfAssessmentQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>updateSelfAssessmentQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Update a self assessment question in the quiz questions.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#updateselfassessmentquestioninput">UpdateSelfAssessmentQuestionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>removeQuestion</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Removes the question with the given number from the quiz.
This will also update the numbers of the following questions.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">number</td>
<td valign="top"><a href="#int">Int</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>switchQuestions</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Switch the position of two questions with the given numbers.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">firstNumber</td>
<td valign="top"><a href="#int">Int</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">secondNumber</td>
<td valign="top"><a href="#int">Int</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>setRequiredCorrectAnswers</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Set the threshold of the quiz, i.e., how many questions the user has to answer correctly to pass the quiz.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">requiredCorrectAnswers</td>
<td valign="top"><a href="#int">Int</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>setQuestionPoolingMode</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Set the question pooling mode of the quiz.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">questionPoolingMode</td>
<td valign="top"><a href="#questionpoolingmode">QuestionPoolingMode</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>setNumberOfRandomlySelectedQuestions</strong></td>
<td valign="top"><a href="#quiz">Quiz</a>!</td>
<td>


Set the number of questions that are randomly selected from the list of questions.
Will only be considered if questionPoolingMode is RANDOM.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">numberOfRandomlySelectedQuestions</td>
<td valign="top"><a href="#int">Int</a>!</td>
<td></td>
</tr>
</tbody>
</table>

### ResourceMarkdown

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>


The raw ResourceMarkdown text.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>referencedMediaRecordIds</strong></td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td>


Ids of MediaRecords referenced in the ResourceMarkdown text in order.

</td>
</tr>
</tbody>
</table>

### SelfAssessmentQuestion

A single question with a free text answer field, where the answer is not automatically checked.
The user has to enter a solution and self-assess whether it is correct or not.
This is useful for questions where the answer is not clear-cut, e.g. when the user should explain a concept.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>solutionSuggestion</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


A possible correct answer to the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


Unique identifier of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


Number of the question, i.e., the position of the question in the list of questions.
Only relevant if questionPoolingMode is ORDERED.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>type</strong></td>
<td valign="top"><a href="#questiontype">QuestionType</a>!</td>
<td>


Type of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### SingleAssociation

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>left</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>


The left side of the association.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>right</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>


The right side of the association.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for the association when the user assigns a wrong answer, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

## Inputs

### AssociationInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>left</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>


Text of the left side of the association, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>right</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>


Text of the right side of the association, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for the association when the user selects a wrong answer, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### ClozeElementInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>type</strong></td>
<td valign="top"><a href="#clozeelementtype">ClozeElementType</a>!</td>
<td>


Type of the element.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Text of the element. Only used for TEXT type.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correctAnswer</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>


The correct answer for the blank. Only used for BLANK type.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for the blank when the user selects a wrong answer, in SlateJS JSON format. Only used for BLANK type.

</td>
</tr>
</tbody>
</table>

### CreateAssociationQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>


Number of the question, used for ordering.
This can be omitted, in which case a number, one higher than the highest number of the existing questions, will be used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correctAssociations</strong></td>
<td valign="top">[<a href="#associationinput">AssociationInput</a>!]!</td>
<td>


List of associations.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### CreateClozeQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>


Number of the question, used for ordering.
This can be omitted, in which case a number, one higher than the highest number of the existing questions, will be used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>clozeElements</strong></td>
<td valign="top">[<a href="#clozeelementinput">ClozeElementInput</a>!]!</td>
<td>


List of cloze elements.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>additionalWrongAnswers</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>


List of additional wrong answers.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>showBlanksList</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


If true, the list of possible answers will be shown to the user.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### CreateExactAnswerQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>


Number of the question, used for ordering.
This can be omitted, in which case a number, one higher than the highest number of the existing questions, will be used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>caseSensitive</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


If the answer is case sensitive. If true, the answer is checked case sensitive.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correctAnswers</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>


A list of possible correct answers.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for the question when the user enters a wrong answer, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### CreateMultipleChoiceQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>


Number of the question, used for ordering.
This can be omitted, in which case a number, one higher than the highest number of the existing questions, will be used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>answers</strong></td>
<td valign="top">[<a href="#multiplechoiceanswerinput">MultipleChoiceAnswerInput</a>!]!</td>
<td>


List of answers.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### CreateNumericQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>


Number of the question, used for ordering.
This can be omitted, in which case a number, one higher than the highest number of the existing questions, will be used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correctAnswer</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>


The correct answer for the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>tolerance</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>


The allowed deviation from the correct answer.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for the question when the user enters a wrong answer, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### CreateQuizInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>requiredCorrectAnswers</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

    Threshold of the quiz, i.e., how many questions the user has to answer correctly to pass the quiz.

    If this is greater than the number of questions, the behavior is the same
    as if it was equal to the number of questions.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>questionPoolingMode</strong></td>
<td valign="top"><a href="#questionpoolingmode">QuestionPoolingMode</a>!</td>
<td>


Question pooling mode of the quiz.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>numberOfRandomlySelectedQuestions</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

    Number of questions that are randomly selected from the list of questions.
    Should only be set if questionPoolingMode is RANDOM.

    If this is greater than the number of questions, the behavior is the same
    as if it was equal to the number of questions.

    If this is null or not set, the behavior is the same as if it was equal to the number of questions.

</td>
</tr>
</tbody>
</table>

### CreateSelfAssessmentQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>


Number of the question, used for ordering.
This can be omitted, in which case a number, one higher than the highest number of the existing questions, will be used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>solutionSuggestion</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


A possible correct answer to the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### DateTimeFilter

Filter for date values.
If multiple filters are specified, they are combined with AND.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>after</strong></td>
<td valign="top"><a href="#datetime">DateTime</a></td>
<td>


If specified, filters for dates after the specified value.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>before</strong></td>
<td valign="top"><a href="#datetime">DateTime</a></td>
<td>


If specified, filters for dates before the specified value.

</td>
</tr>
</tbody>
</table>

### IntFilter

Filter for integer values.
If multiple filters are specified, they are combined with AND.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>equals</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>


An integer value to match exactly.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>greaterThan</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>


If specified, filters for values greater than to the specified value.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>lessThan</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>


If specified, filters for values less than to the specified value.

</td>
</tr>
</tbody>
</table>

### MultipleChoiceAnswerInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>answerText</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the answer, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correct</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


Whether the answer is correct or not.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for when the user selects this answer, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### Pagination

Specifies the page size and page number for paginated results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>page</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


The page number, starting at 0.
If not specified, the default value is 0.
For values greater than 0, the page size must be specified.
If this value is larger than the number of pages, an empty page is returned.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>size</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


The number of elements per page.

</td>
</tr>
</tbody>
</table>

### QuestionCompletedInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>questionId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


ID of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correct</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


true when question was answered correctly

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>usedHint</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


true when a hint was used for the question

</td>
</tr>
</tbody>
</table>

### QuizCompletedInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>quizId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


ID of the quiz.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>completedQuestions</strong></td>
<td valign="top">[<a href="#questioncompletedinput">QuestionCompletedInput</a>!]!</td>
<td>


List of questions that were answered in the quiz.

</td>
</tr>
</tbody>
</table>

### ResourceMarkdownInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>


The raw ResourceMarkdown text.

</td>
</tr>
</tbody>
</table>

### StringFilter

Filter for string values.
If multiple filters are specified, they are combined with AND.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>equals</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>


A string value to match exactly.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>contains</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>


A string value that must be contained in the field that is being filtered.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>ignoreCase</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


If true, the filter is case-insensitive.

</td>
</tr>
</tbody>
</table>

### UpdateAssociationQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


UUID of the question to update.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correctAssociations</strong></td>
<td valign="top">[<a href="#associationinput">AssociationInput</a>!]!</td>
<td>


List of associations.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### UpdateClozeQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


UUID of the question to update.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>clozeElements</strong></td>
<td valign="top">[<a href="#clozeelementinput">ClozeElementInput</a>!]!</td>
<td>


List of cloze elements.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>additionalWrongAnswers</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>


List of additional wrong answers.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>showBlanksList</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


If true, the list of possible answers will be shown to the user.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### UpdateExactAnswerQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


UUID of the question to update.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correctAnswers</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>


A list of possible correct answers.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>caseSensitive</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>


If the answer is case sensitive. If true, the answer is checked case sensitive.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for the question when the user enters a wrong answer, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### UpdateMultipleChoiceQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


UUID of the question to update.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>answers</strong></td>
<td valign="top">[<a href="#multiplechoiceanswerinput">MultipleChoiceAnswerInput</a>!]!</td>
<td>


List of answers.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### UpdateNumericQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


UUID of the question to update.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>correctAnswer</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>


The correct answer for the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>tolerance</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>


The allowed deviation from the correct answer.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>feedback</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Feedback for the question when the user enters a wrong answer, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

### UpdateSelfAssessmentQuestionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


UUID of the question to update.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>text</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


Text of the question, in SlateJS JSON format.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>solutionSuggestion</strong></td>
<td valign="top"><a href="#json">JSON</a>!</td>
<td>


A possible correct answer to the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

## Enums

### ClozeElementType

<table>
<thead>
<th align="left">Value</th>
<th align="left">Description</th>
</thead>
<tbody>
<tr>
<td valign="top"><strong>TEXT</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>BLANK</strong></td>
<td></td>
</tr>
</tbody>
</table>

### QuestionPoolingMode

<table>
<thead>
<th align="left">Value</th>
<th align="left">Description</th>
</thead>
<tbody>
<tr>
<td valign="top"><strong>RANDOM</strong></td>
<td>


Questions are randomly selected from the list of questions.

</td>
</tr>
<tr>
<td valign="top"><strong>ORDERED</strong></td>
<td>


Questions are selected in order from the list of questions.

</td>
</tr>
</tbody>
</table>

### QuestionType

The type of a question.

<table>
<thead>
<th align="left">Value</th>
<th align="left">Description</th>
</thead>
<tbody>
<tr>
<td valign="top"><strong>MULTIPLE_CHOICE</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>CLOZE</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>ASSOCIATION</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>EXACT_ANSWER</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>NUMERIC</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>SELF_ASSESSMENT</strong></td>
<td></td>
</tr>
</tbody>
</table>

### SortDirection

Specifies the sort direction, either ascending or descending.

<table>
<thead>
<th align="left">Value</th>
<th align="left">Description</th>
</thead>
<tbody>
<tr>
<td valign="top"><strong>ASC</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>DESC</strong></td>
<td></td>
</tr>
</tbody>
</table>

## Scalars

### Boolean

Built-in Boolean

### Date

An RFC-3339 compliant Full Date Scalar

### DateTime

A slightly refined version of RFC-3339 compliant DateTime Scalar

### Float

Built-in Float

### Int

Built-in Int

### JSON

A JSON scalar

### LocalTime

24-hour clock time value string in the format `hh:mm:ss` or `hh:mm:ss.sss`.

### String

Built-in String

### Time

An RFC-3339 compliant Full Time Scalar

### UUID

A universally unique identifier compliant UUID Scalar

### Url

A Url scalar

## Interfaces

### Question

Generic question interface.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>


Unique identifier of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>number</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>


Number of the question, i.e., the position of the question in the list of questions.
Only relevant if questionPoolingMode is ORDERED.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>type</strong></td>
<td valign="top"><a href="#questiontype">QuestionType</a>!</td>
<td>


Type of the question.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hint</strong></td>
<td valign="top"><a href="#json">JSON</a></td>
<td>


Optional hint for the question, in SlateJS JSON format.

</td>
</tr>
</tbody>
</table>

## Unions

### ClozeElement

<table>
<thead>
<th align="left">Type</th>
<th align="left">Description</th>
</thead>
<tbody>
<tr>
<td valign="top"><strong><a href="#clozetextelement">ClozeTextElement</a></strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong><a href="#clozeblankelement">ClozeBlankElement</a></strong></td>
<td></td>
</tr>
</tbody>
</table>
