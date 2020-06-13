name := "seanmcapp"
version := "latest"
scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  // framework
  "com.typesafe.akka" %% "akka-http" % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.6.3",

  // json serializer
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",

  // http builder
  "org.scalaj" %% "scalaj-http" % "2.4.2",

  // ORM
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
  "org.slf4j" % "slf4j-nop" % "1.7.30",

  // postgresql
  "org.postgresql" % "postgresql" % "42.2.5",

  // scalatest
  "org.scalatest" %% "scalatest" % "3.1.1" % "test,it",

  // mockito
  "org.mockito" % "mockito-core" % "3.3.0" % Test,

  // joda time
  "joda-time" % "joda-time" % "2.10.5",

  // caching
  "com.github.cb372" %% "scalacache-guava" % "0.28.0",

  // image storage
  "io.minio" % "minio" % "6.0.13",

  // xml
  "org.scala-lang.modules" %% "scala-xml" % "1.3.0",

  // enum
  "com.beachape" %% "enumeratum" % "1.5.15"

  // testkit
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.3",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.11"
)

coverageExcludedPackages :=
  ".*com.seanmcapp.util.*;" +
    ".*com.seanmcapp.config.*;" +
    ".*Boot.*;.*Route.*;.*Injection.*;.*ScheduleManager.*;"
coverageMinimum := 80
coverageFailOnMinimum := true

fork in Test := true
fork in IntegrationTest := true
configs(IntegrationTest)
Defaults.itSettings
javaOptions in IntegrationTest += "-Dconfig.resource=application-local.conf"

/**
 *  DOCKERIZE
 *  publish: sbt docker:publishLocal
 *  run: docker run --env-file=.env -p 9000:9000 seanmcapp
 */

mainClass in Compile := Some("com.seanmcapp.Boot")
dockerBaseImage := "openjdk:jre-alpine"
dockerRepository := Some("seanmcrayz")

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)