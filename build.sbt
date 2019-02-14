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

  // mysql
  "mysql" % "mysql-connector-java" % "6.0.6",

  // scalatest
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  
  // image storage
  "com.amazonaws" % "aws-java-sdk" % "1.11.444",

  // caching
  "com.github.cb372" %% "scalacache-guava" % "0.27.0"
)

fork in Test := true
javaOptions in Test += "-Dconfig.resource=/dev.conf"

mainClass in Compile := Some("com.seanmcapp.startup.Boot")

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)