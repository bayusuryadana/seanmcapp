name := "seanmcapp"

version := "0.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  // framework
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",

  // json serializer
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3",

  // http builder
  "org.scalaj" %% "scalaj-http" % "2.4.0",

  //ORM
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",

  // DB Driver
  "org.postgresql" % "postgresql" % "42.1.3",
  "com.h2database" % "h2" % "1.4.192",

  // scalatest
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.mockito" % "mockito-core" % "2.20.1" % Test
)

fork in Test := true
javaOptions in Test += "-Dconfig.resource=/dev.conf"

enablePlugins(JavaAppPackaging)