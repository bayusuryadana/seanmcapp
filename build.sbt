name := "seanmcapp"

version := "0.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.0-RC2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0-RC2",
  "org.scalaj" % "scalaj-http_2.12" % "2.3.0",
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "org.postgresql" % "postgresql" % "42.1.3"
)

enablePlugins(JavaAppPackaging)