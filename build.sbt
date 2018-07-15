name := "naive-workflow-manager"

val versions = new {
  val akkaHttp = "10.1.3"
  val akka = "2.5.12"
  val app = "0.1"
  val jdbc = "3.2.2"
  val mysqlConnector = "5.1.38"
  val scala = "2.12.6"
  val scalaMock = "4.1.0"
  val scalaTest = "3.0.5"
}

version := versions.app

scalaVersion := versions.scala

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % versions.akkaHttp,
  "com.typesafe.akka" %% "akka-http-spray-json" % versions.akkaHttp,
  "com.typesafe.akka" %% "akka-stream" % versions.akka,
  "com.typesafe.akka" %% "akka-testkit" % versions.akka % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % versions.akkaHttp % Test,
  "mysql" % "mysql-connector-java" % versions.mysqlConnector,
  "org.scalikejdbc" %% "scalikejdbc" % versions.jdbc,
  "org.scalatest" %% "scalatest" % versions.scalaTest % Test,
  "org.scalamock" %% "scalamock" % versions.scalaMock % Test
)
