import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "cuf"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    // Add your project dependencies here,
    "org.neo4j.app" % "neo4j-server" % "1.6",
    "org.neo4j.app" % "neo4j-server" % "1.6" classifier "static-web",
    "com.sun.jersey" % "jersey-core" % "1.9"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    // Add your own project settings here      
    resolvers ++= Seq(
      "tinkerprop" at "http://tinkerpop.com/maven2",
      "neo4j-public-repository" at "http://m2.neo4j.org/releases")
    )

}
