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
  "org.scalactic" %% "scalactic" % "3.0.5" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  
  // joda time
  "joda-time" % "joda-time" % "2.10.1",

  // caching
  "com.github.cb372" %% "scalacache-guava" % "0.27.0"
)

fork in Test := true
// javaOptions in Test += "-Dconfig.resource=/dev.conf"

mainClass in Compile := Some("com.seanmcapp.Boot")

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)