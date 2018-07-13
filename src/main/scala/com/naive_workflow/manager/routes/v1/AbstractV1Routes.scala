package com.naive_workflow.manager.routes.v1

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.naive_workflow.manager.database.WorkflowDAOInterface

// daniel abstract?
// daniel what's the point of abstract classes?
trait AbstractV1Routes extends AbstractV1WorkflowRoutes with AbstractV1WorkflowExecutionRoutes {

  implicit def timeout: Timeout

  // daniel prefix individual routes with V1
  // daniel these are in theory all workflowRoutes, are they not?
  val routes: Route =
    pathPrefix("v1") {
      v1WorkflowRoutes ~
      v1WorkflowExecutionRoutes
    }
}
