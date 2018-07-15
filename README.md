# App Architecture

The app is a fairly standard scala server that uses akka http, akka actors, and mysql.  
It is possibe to run the application from the command line and to make requests against it using curl 

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
docker-compose --file=docker-compose.yml --file=docker-compose.crond.yml build
docker-compose --file=docker-compose.yml --file=docker-compose.crond.yml up
sbt run
```

# Run Tests

// daniel TODO
```bash
docker-compose --file=docker-compose.yml build
docker-compose --file=docker-compose.yml up
sbt test
```

# Run "Production-like"

// daniel TODO
```bash
docker-compose --file=docker-compose.yml --file=docker-compose.prod.yml up
```

# Routes

// daniel add GET route /v1/workflows/:id/executions?
```text
curl -X POST /v1/jobs/delete-terminated-workflow-executions

curl -X GET  /v1/workflows
curl -X POST /v1/workflows

curl -X POST /v1/workflows/:workflowId/executions
curl -X POST /v1/workflows/:workflowId/executions/:executionId/incrementations
```

# Periodic Jobs

// daniel crond documentation


// STRONG
// daniel test everything from scratch
// daniel create unit tests, integration tests, docker-compose.test.yml, ./tests.sh
// daniel rework the whole git history

// AVERAGE
// daniel eliminate compiling warnings/errors
// daniel add more meaningful comments
// daniel Dockerfile, "./publish.sh", and "production-like" working
// daniel "/healthcheckz" endpoint
// daniel can sbt flag when imports are unused?

// WEAK
// daniel what is a dispatcher?
// daniel how on earth does execution context work?
// daniel login (actors, app errors) in general?
// daniel function toDateTime(s: String): Datetime, in com.naive_workflow.utils ??
// daniel add EitherT

// NEW TODOS GO HERE
// Make architecture doc less verbose, potentially add it here
