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
  
  // joda time
  "joda-time" % "joda-time" % "2.10.1",

  // caching
  "com.github.cb372" %% "scalacache-guava" % "0.27.0",

  // image storage
  "com.amazonaws" % "aws-java-sdk" % "1.11.592"
)

fork in Test := true
fork in IntegrationTest := true
configs(IntegrationTest)
Defaults.itSettings
javaOptions in IntegrationTest += "-Dconfig.resource=application-local.conf"

/**
  *  DOCKERIZE
  *  publish: sbt docker:publishLocal
  *  run: docker run --env-file=.env -p 9000:9000 seanmcapp
  *  TODO: need `docker login` before pushing
  */

mainClass in Compile := Some("com.seanmcapp.Boot")
dockerBaseImage := "openjdk:jre-alpine"
dockerRepository := Some("seanmcrayz")

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)