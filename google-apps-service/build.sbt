name := """google-apps-service"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
		javaWs,
		"com.orientechnologies" % "orientdb-core" % "2.1.6",
		"com.orientechnologies" % "orientdb-graphdb" % "2.1.6",
		"com.google.api-client" % "google-api-client" % "1.21.0",
		"com.google.apis" % "google-api-services-oauth2" % "v2-rev101-1.21.0",
		"com.google.apis" % "google-api-services-admin-directory" % "directory_v1-rev63-1.21.0",
		"com.google.oauth-client" % "google-oauth-client-java6" % "1.21.0",
		"com.google.oauth-client" % "google-oauth-client-jetty" % "1.21.0",
		"com.google.oauth-client" % "google-oauth-client" % "1.21.0",
		"com.google.apis" % "google-api-services-admin" % "reports_v1-rev26-1.16.0-rc",
		"com.google.apis" % "google-api-services-gmail" % "v1-rev36-1.21.0"
		)


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
