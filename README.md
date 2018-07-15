// daniel rework the whole git history  
// daniel add more meaningful comments  
// daniel one last check that everything works

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
- The data is kept almost entirely immutable
- Side effects are handled at the edge of the application (inside DatabaseAccessObjects)


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

```bash
docker-compose --file=docker-compose.yml --file=docker-compose.crond.yml build
docker-compose --file=docker-compose.yml --file=docker-compose.crond.yml up
sbt run
```


# HTTP Routes

```text
curl -X GET  /v1/workflows
curl -X POST /v1/workflows

curl -X GET  /v1/workflows/:workflowId/executions
curl -X POST /v1/workflows/:workflowId/executions
curl -X POST /v1/workflows/:workflowId/executions/:executionId/incrementations

curl -X POST /v1/jobs/delete-terminated-workflow-executions
```


# Architecture Log Document

[architecture-log](./docs/architecture-log.md)


# Further Improvements

## STRONG
- more QA
- e2e tests
- healthcheckz endpoint
- add logging for http requests and actors

## AVERAGE
- ./publish.sh do publish image to of the naiveworkflow app dockerhub
- configure sbt to flag unused imports
- learn more about akka http
- learn more about actor model (akka/jvm in particular)
- use EitherT in the route handlers

## WEAK
- learn more about akka dispatchers
- learn more about execution context
- Utils classes are candidates for property-based testing
