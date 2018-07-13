package com.naive_workflow.manager.routes.v1

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout

// daniel abstract?
trait AbstractV1Routes extends AbstractV1WorkflowRoutes with AbstractV1WorkflowExecutionRoutes {

  implicit def timeout: Timeout

  lazy val routes: Route =
    pathPrefix("v1") {
      v1WorkflowRoutes ~
      v1WorkflowExecutionRoutes
    }
}
