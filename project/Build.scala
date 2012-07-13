import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "cuf"
    val appVersion      = "1.0-SNAPSHOT"
      
    val NEO4J_VERSION 	= "1.7.1"

    val appDependencies = Seq(
    // Add your project dependencies here,
    "org.neo4j.app" % "neo4j-server" % NEO4J_VERSION,
    "org.neo4j.app" % "neo4j-server" % NEO4J_VERSION classifier "static-web",
    "org.neo4j" % "neo4j-kernel" % NEO4J_VERSION % "test" classifier "tests",
    "com.sun.jersey" % "jersey-core" % "1.9",
    "org.scalatest" %% "scalatest" % "1.7.2" % "test"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    // Add your own project settings here
    resolvers ++= Seq(
      "tinkerprop" at "http://tinkerpop.com/maven2",
      "neo4j-public-repository" at "http://m2.neo4j.org/releases",
      "maven" at "http://download.java.net/maven/2/"),

    testOptions in Test := Nil

    )


}
