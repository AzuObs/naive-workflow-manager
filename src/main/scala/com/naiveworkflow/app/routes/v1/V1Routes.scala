package com.naiveworkflow.app.routes.v1

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout

object V1Routes {

  def routes(workflowActor: ActorRef, executionActor: ActorRef)(implicit timeout: Timeout): Route =
    pathPrefix("v1") {
      V1WorkflowRoutes.routes(workflowActor) ~
      V1WorkflowExecutionRoutes.routes(executionActor) ~
      V1JobRoutes.routes(executionActor)
    }

}
