name := """Thunderbit"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  evolutions,
  "org.postgresql" % "postgresql" % "9.4.1207",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.10.44",
  "org.webjars" %% "webjars-play" % "2.4.0-2",
  "org.webjars" % "requirejs" % "2.1.22",
  "org.webjars" % "jquery" % "1.11.1",
  "org.webjars" % "bootstrap" % "3.3.6",
  "org.webjars" % "backbonejs" % "1.2.3",
  "org.webjars" % "underscorejs" % "1.8.3",
  "org.webjars" % "typeaheadjs" % "0.11.1",
  "net.sf.flexjson" % "flexjson" % "3.3",
  "be.objectify" %% "deadbolt-java" % "2.4.4"
)

libraryDependencies += "junit" % "junit" % "4.11" % Test

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
