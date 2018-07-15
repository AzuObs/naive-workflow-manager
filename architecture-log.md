# Architecture Log
The purpose of the architecture log is to log the reasons why certain architectural decisions were made.


## 1) 07/07/2018 Ideal AWS System Architecture

[Architecture Schema](https://drive.google.com/file/d/1GWZ8qDlr_1ihV0J5J72LhmL0IjpvP-b2/view?usp=sharing)

The main goal of the design is to allow the system to tolerate high loads.

Requests first enter the system via a Workflow Service (EC2 auto scaling). The services handles the various requests by publishing messages to the SNS queue and while returning a 2xx response to the caller. The main interface would be a versioned HTTP REST api, but it could also include a GraphQL interface that calls the same controller methods.

SNS then pushes its message to one of two different SQS queues.

The Lambda consumers will pull as many messages as they can from the SQS queues and invoke workers to handle the messages.

The workers will perform some computations and save the resources to a relational database (PostgreSQL, MySQL).

There is a CloudWatch scheduler that invokes a WorkflowExecutionCleanupWorker every minute.


## 2) 15/07/2018 Pragmatic Service Architecture

The com.naiveworkflow app is a fairly standard scala server that uses akka http, akka actors, and mysql.  
It is possible to run the application from the command line and to make requests against it  
using curl  

This is the high-level lifecycle of a request:
- Requests are received in the Route handlers
- Route handler dispatches a message to an Actor
- Actor uses a Service
- Service uses a DatabaseAccessObject
- DatabaseAccessObject does IO with the database

Functional:
- The data is kept almost entirely immutable
- Side effects are handled at the edge of the application (inside DatabaseAccessObjects)
