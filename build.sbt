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

  // DB Driver
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.4.1",
  "io.netty" % "netty-all" % "4.1.28.Final",

  // scalatest
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  
  // image storage
  "com.amazonaws" % "aws-java-sdk" % "1.11.444"
)

mainClass in Compile := Some("com.seanmcapp.startup.Boot")

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)