# Architecture Log
The purpose of the architecture log is to log the reasons why certain architectural decisions were made.


## 1) 07/07/2018 Ideal AWS System Architecture

The design of our system is schemed [herein](https://drive.google.com/file/d/1GWZ8qDlr_1ihV0J5J72LhmL0IjpvP-b2/view?usp=sharing). The main goal of the design is to allow the system to tolerate high loads.


### 1.1) AWS System Design Overview

Requests first enter the system via a Workflow Service (EC2 auto scaling). The services handles the various requests by publishing messages to the SNS queue and while returning a 2xx response to the caller. The main interface would be a versioned HTTP REST api, but it could also include a GraphQL interface that calls the same controller methods.

SNS then pushes it's message to one of two different SQS queues.

The Lambda consumers will pull as many messages as they can from the SQS queues and invoke workers to handle the messages.

The workers will perform some computations and save the resources to a relational database (PostgreSQL, MySQL).

There is a CloudWatch scheduler that invokes a WorkflowExecutionCleanupWorker every minute.


### 1.1.1) STRENGTHS

- Highly available

- Decoupled

- Poison pills are handled with the Dead Letter queue

- Resilient


### 1.1.2) WEAKNESSES

- Our system is eventually consistent, this introduces some complexity. For example, the Workflow Service when receiving POST /v1/workflows would publish a message to a SNS topic with "{message: 'CreateWorkflow', data: {workflowUUID: 123...}}" and would return a "{status:201, data:{workflowUUID: 123...}}" to the caller. This resource does however not yet exist and the caller would have to keep polling until the resource is created(there are other strategies such as Server Side Events that would probably be better).

- Lambda workers cannot maintain connection pools to their database (slow, and expensive for the database).

- Relational databases might not scale enough: 
  * There are solution to manage scaling relational databases (read replicas, horizontal partitioning, vertical partitioning, splitting the database into two `workflow` and `workflow-executions` databases).
  * NoSQL solutions could be considered

- SQS queues introduce duplicated messages. This means we would have to design our system to tolerate these duplicates. One way to handle this edge case is to implement data as events and use an event sourcing approach. 
  * Take for example the scenario where the SQS message "WorkflowExecutionIncrementationCreation" is duplicated, if we used a naive approach and simply incremented the `workflow_execution.currentStep` in the `workflow_execution` table, the duplicate event would increment this value twice which is wrong.
  * The for example a scenario where we are using an event sourcing strategy where the SQS message "WorkflowExecutionIncrementationCreation with eventUUID=abc123..." is duplicated. When inserting this event into the `workflow_execution_events` table, MySQL would not insert the event because of the UNIQUE constraint for eventUUID. Even if we were using a database that did not have such constraints, when building the state of the WorkflowExecution from its events, the algorithm would simply filter out events with duplicate UUIDs. As a side node, the CQRS optimisation to event sourcing should also be considered.
