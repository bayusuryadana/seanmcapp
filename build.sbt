name := "seanmcapp"
version := "latest"
scalaVersion := "2.13.16"
maintainer := "seanmcrayz@yahoo.com"

resolvers += Resolver.JCenterRepository
libraryDependencies ++= Seq(
  // framework
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  "com.typesafe.akka" %% "akka-stream" % "2.8.7",

  // testkit
  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.3" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.8.7" % Test,

  // parallel collection
//  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",

  // json serializer*
  "io.circe" %% "circe-core" % "0.14.9",
  "io.circe" %% "circe-generic" % "0.14.10",
  "io.circe" %% "circe-parser" % "0.14.10",

  // http builder*
  "org.scalaj" %% "scalaj-http" % "2.4.2",

  // ORM
  "com.typesafe.slick" %% "slick" % "3.5.2",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.5.2",
  "org.slf4j" % "slf4j-nop" % "2.0.16",

  // postgresql
  "org.postgresql" % "postgresql" % "42.7.4",

  // scalatest
  "org.scalatest" %% "scalatest" % "3.2.19" % "test,it",

  // mockito
  "org.mockito" % "mockito-core" % "5.14.2" % Test,

  // joda time
  "joda-time" % "joda-time" % "2.13.0",
  "org.joda"  % "joda-convert" % "2.2.3",

  // caching
  "com.github.cb372" %% "scalacache-guava" % "0.28.0",

  // cron
  "com.github.alonsodomin.cron4s" %% "cron4s-core" % "0.7.0",
  "com.github.alonsodomin.cron4s" %% "cron4s-joda" % "0.7.0",

  // jsoup (Java HTML parser)
  "org.jsoup" % "jsoup" % "1.18.1",

  // enum
  "com.beachape" %% "enumeratum" % "1.7.5",
)

coverageExcludedPackages :=
  ".*com.seanmcapp.util.*;" +
  ".*com.seanmcapp.config.*;" +
  ".*Boot.*;" +
  ".*com.seanmcapp.*html.*;"

coverageMinimum := 90
coverageFailOnMinimum := true

fork in Test := true
fork in IntegrationTest := true
configs(IntegrationTest)
Defaults.itSettings
javaOptions in IntegrationTest += "-Dconfig.resource=application-local.conf"
mainClass in Compile := Some("com.seanmcapp.Main")

/**
  *  DOCKER
  *  publish: sbt docker:publishLocal
  *  run: docker run --env-file=.env -p 9000:9000 seanmcapp
  */
mainClass in Compile := Some("com.seanmcapp.Boot")
dockerBaseImage := "openjdk:jre-alpine"
dockerRepository := Some("docker.pkg.github.com/bayusuryadana/seanmcapp")

enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin, SbtTwirl)
