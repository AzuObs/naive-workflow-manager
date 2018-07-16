# App Architecture

The app is a fairly standard scala server that uses akka http, akka actors, and mysql.  
It is possible to run the application from the command line and to make requests against it  
using curl 

This is the high-level lifecycle of a request:
- Requests are received in the Route handlers
- Route handler dispatches a message to an Actor
- Actor uses a Service
- Service uses a DatabaseAccessObject

Functional:
- The data is almost entirely immutable
- Side effects are handled at the edge of the application (inside DatabaseAccessObjects)


# Dependencies

The project assumes you have installed:
- Java 8 JDK
- Scala compiler 2.12.1+ 
- sbt
- Docker
- Docker-compose


# Run Dev

```bash
docker-compose build
docker-compose up
sbt run
```


# Run Tests

```bash
docker-compose build
docker-compose up
sbt test
```


# Periodic Jobs

Periodic job run in the `crond` docker container. There is a `docker/crond/config.json` describing the periodic job being run.


# HTTP Routes

```text
curl -X GET  localhost:8080/v1/workflows
curl -X POST localhost:8080/v1/workflows -H "Content-Type: application/json" --data '{"nSteps": 3}'

curl -X GET  localhost:8080/v1/workflows/:workflowId/executions
curl -X POST localhost:8080/v1/workflows/:workflowId/executions
curl -X POST localhost:8080/v1/workflows/:workflowId/executions/:executionId/incrementations

curl -X POST localhost:8080/v1/jobs/delete-terminated-workflow-executions
```


# Architecture Log Document

[architecture-log](./docs/architecture-log.md)


# Further Improvements

## Strong
- more QA
- e2e tests
- healthcheckz endpoint
- add logging for http requests and actors

## Average
- ./publish.sh do publish image to of the naiveworkflow app dockerhub
- create an APIResponse JSON formatted as `{"data": {}, "errors": []}` for each endpoint
- configure sbt to flag unused imports
- learn more about akka http
- learn more about actor model (akka/jvm in particular)
- use EitherT in the route handlers

## Weak
- learn more about akka dispatchers
- learn more about execution context
- Utils classes are candidates for property-based testing
