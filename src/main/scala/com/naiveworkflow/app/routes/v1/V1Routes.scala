package com.naiveworkflow.app.routes.v1

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout

trait V1Routes extends V1WorkflowRoutes with V1WorkflowExecutionRoutes with V1JobRoutes {

  implicit def timeout: Timeout

  val routes: Route =
    pathPrefix("v1") {
      v1WorkflowRoutes ~
      v1WorkflowExecutionRoutes ~
      v1JobRoutes
    }

}
