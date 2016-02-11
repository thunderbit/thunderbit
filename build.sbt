name := """Thunderbit"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

resolvers += "ylemoigne-maven" at "https://dl.bintray.com/ylemoigne/maven/"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.mongodb" % "mongodb-driver-async" % "3.2.0",
  "fr.javatic.mongo" % "mongo-jackson-codec" % "3.2.0__0.3",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.10.44",
  "org.webjars" %% "webjars-play" % "2.4.0-2",
  "org.webjars" % "requirejs" % "2.1.22",
  "org.webjars" % "jquery" % "1.11.1",
  "org.webjars" % "bootstrap" % "3.3.6",
  "be.objectify" %% "deadbolt-java" % "2.4.4"
)

libraryDependencies += "junit" % "junit" % "4.11" % Test

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
