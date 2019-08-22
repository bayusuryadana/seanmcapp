name := "seanmcapp"
version := "latest"
scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  // framework
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",

  // json serializer
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3",

  // http builder
  "org.scalaj" %% "scalaj-http" % "2.4.0",

  // ORM
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",

  // postgresql
  "org.postgresql" % "postgresql" % "42.2.5",

  // scalatest
  "org.scalatest" %% "scalatest" % "3.0.5" % "test,it",

  // mockito
  "org.mockito" % "mockito-core" % "3.0.0" % Test,
  
  // joda time
  "joda-time" % "joda-time" % "2.10.1",

  // caching
  "com.github.cb372" %% "scalacache-guava" % "0.27.0",

  // image storage
  "io.minio" % "minio" % "6.0.8",
)

coverageEnabled := true
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