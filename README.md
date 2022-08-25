# BA2 interview exercise: Java, Spring Boot, jOOQ/SQL, Docker

This exercise is aimed at testing your practical knowledge of Java, Spring Boot, jOOQ/SQL, and Docker with successive
tasks from a simple base project. It uses an H2 database, similar to SQLite.

The starter project was adapted from <https://github.com/jOOQ/jOOQ/tree/main/jOOQ-examples/jOOQ-spring-boot-example>
(Apache License) and modified to be standalone.

## Rules

- We encourage you to ask questions about the tasks at the beginning. After that if a requirement is not clear choose
  your own interpretation.
- Use your preferred tooling (IDE, editor, ...) but the Maven (with or without Maven wrapper) commands of the next
  section should still run in the CLI.
- You can add extra libraries if you want (e.g. for testing). Same remark as above for Maven.
- Looking up things online is totally allowed and probably necessary. However, if you copy code you should be able to
  explain it.
- Do as much as you can within the given time frame, but it's better to have complete tasks rather of a bit of each but
  nothing working.
- Be prepared to demo and walk us through your solution and answer questions about it.

## Running the example

The basic example and its test suite should run unmodified at the beginning of the exercise. You will need:

- JDK 17
- A shell to run the maven wrapper, or Maven itself installed
- Docker

If your environment is OK, the following should compile, run tests and package the application:

```sh
# On MacOS/Linux
./mvnw clean package

# On Windows
mvnw.cmd clean package

# If you have Maven 3+ installed on your machine you can also use it
mvn clean package
```

If it doesn't run, we'll give some time to fix the environment and the resolution of the problems can be part of the
debrief after the exercise.

Note: the H2 DB is created in your home directory.

## Tasks

### 1. Add a method to search books by author

The goal here is to add a new method to `DefaultBookService`. This method should allow to search books by author.
Based on a provided search string, it should return all books with a matching author.

This example project uses [jOOQ](https://www.jooq.org/). If you don't know it you can either:

- Use the documentation or other online resources to build your query. Code generation is already setup, so it should be
  simple enough to get started. An IDE with autocomplete is useful here.
- Write plain SQL, see <https://www.jooq.org/doc/latest/manual/getting-started/use-cases/jooq-as-a-sql-executor/> to
  run it with jOOQ. You can also use plain JDBC and disregard jOOQ completely if you want.

**JPA however is not allowed**. We want to test your SQL conceptual knowledge and JPA abstracts it too much.

### 2. Add an HTTP API endpoint

Add an HTTP endpoint which uses the previously implemented method to search books by author. It's an API endpoint,
so it should return the data in a structured format.

### 3. Create a unit test for the search by author service method

Implement a realistic test for the author service method to ensure it is working properly. You don't need full coverage,
but the test you write should make sense.

The test should be implemented with the Spring tooling and integrated in Maven.

### 4. Create a unit test for the search by author API endpoint

Implement a realistic "integration" unit test for the API endpoint to ensure it is working properly. You don't need full
coverage, but the test you write should make sense.

The test should be implemented with the Spring tooling and integrated in Maven.

### 5. Package the application as a Docker image

Write a Dockerfile to package the application. Prepare simple command lines to build and run your container.
