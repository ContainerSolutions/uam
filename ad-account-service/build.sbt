name := """ad-account-service"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.unboundid" % "unboundid-ldapsdk" % "3.1.0"
  , "org.mockito" % "mockito-all" % "1.10.19" % "test"
  , "com.typesafe.play" %% "play-java-ws" % "2.4.3" % "test"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator