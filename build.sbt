name := "naive-workflow-manager"

val versions = new {
  val akkaHttp = "10.1.3"
  val akka = "2.5.12"
  val app = "0.1"
  val jdbc = "3.1.0"
  val mysqlConnector = "5.1.38"
  val scala = "2.12.6"
}

version := versions.app

scalaVersion := versions.scala

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % versions.akkaHttp,
  "com.typesafe.akka" %% "akka-http-spray-json" % versions.akkaHttp,
  "com.typesafe.akka" %% "akka-stream" % versions.akka, // daniel needed?
  "org.scalikejdbc" %% "scalikejdbc" % versions.jdbc,
  "mysql" % "mysql-connector-java" % versions.mysqlConnector
)
