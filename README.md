# Quiz Service



The Quiz Service in GITS offers a variety of quiz types to facilitate learning and assessment. These quizzes include:

### Multiple Choice Quiz

In a Multiple Choice Quiz, users must answer a set of questions. Each question presents multiple answers, with one or more being correct. Users are required to select all correct answers.

### Cloze Quiz

The Cloze Quiz prompts users to fill in the blanks within a given text, testing their understanding of the content.

### Self-Assessment Quiz

The Self-Assessment Quiz presents users with questions that require free-text responses. Users can later compare their answers with correct solutions, similar to flashcards.

### AssociationQuestion

In an Association Question, users are tasked with establishing associations between different elements. This type of quiz challenges users to match items from two or more columns, identifying correct pairings.

### ExactAnswerQuestion

The Exact Answer Question presents users with questions that require precise, specific responses. Users need to provide answers that precisely match predefined correct solutions.

### NumericQuestion

In a Numeric Question, users must respond with numerical values. This quiz type is used for questions that demand numeric answers, such as mathematical calculations or numerical data input.

## Environment variables
### Relevant for deployment
| Name                       | Description                        | Value in Dev Environment                       | Value in Prod Environment                                      |
|----------------------------|------------------------------------|------------------------------------------------|----------------------------------------------------------------|
| spring.datasource.url               | PostgreSQL database URL            | jdbc:postgresql://localhost:9032/quiz_service* | jdbc:postgresql://quiz-service-db-postgresql:5432/quiz-service |
| spring.datasource.username          | Database usernam                   | root                                           | gits                                                           |
| spring.datasource.password          | Database password                  | root                                           | *secret*                                                       |
| DAPR_HTTP_PORT                        | Dapr HTTP Port*                        | 9000                                           | 3500                                                              |
| server.port                           | Port on which the application runs     | 9001                                           | 9001                                                              |
### Other properties

| Name                                    | Description                               | Value in Dev Environment                | Value in Prod Environment               |
|-----------------------------------------|-------------------------------------------|-----------------------------------------|-----------------------------------------|
| spring.graphql.graphiql.enabled         | Enable GraphiQL web interface for GraphQL | true                                    | true                                    |
| spring.graphql.graphiql.path            | Path for GraphiQL when enabled            | /graphiql                               | /graphiql                               |
| spring.profiles.active                  | Active Spring profile                     | dev                                     | prod                                    |
| spring.jpa.properties.hibernate.dialect | Hibernate dialect for PostgreSQL          | org.hibernate.dialect.PostgreSQLDialect | org.hibernate.dialect.PostgreSQLDialect |
| spring.datasource.driver-class-name     | JDBC driver class                         | org.postgresql.Driver                   | org.postgresql.Driver                   |
| spring.sql.init.mode                    | SQL initialization mode                   | always                                  | always                                  |
| spring.jpa.show-sql                     | Show SQL queries in logs                  | true                                    | true                                    |
| spring.sql.init.continue-on-error       | Continue on SQL init error                | true                                    | true                                    |
| spring.jpa.hibernate.ddl-auto           | Hibernate DDL auto strategy               | create                                  | update                                  |
| logging.level.root                      | Logging level for root logger             | DEBUG                                   | -                                       |
| DAPR_GRPC_PORT                          | Dapr gRPC Port                            | -                                       | 50001                                   |

## API description

The GraphQL API is described in the [api.md file](api.md).

The endpoint for the GraphQL API is `/graphql`. The GraphQL Playground is available at `/graphiql`.

## Get started

A guide how to start development can be
found [wiki](https://gits-enpro.readthedocs.io/en/latest/dev-manuals/backend/get-started.html).


