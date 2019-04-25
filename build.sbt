name := "sbtblog"
 
version := "1.0"

maintainer := "zhaopinchn@hotmail.com"

lazy val `sbtblog` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  jdbc ,
  ehcache ,
  ws ,
  specs2 % Test ,
  guice  ,
  evolutions,
  "mysql" % "mysql-connector-java" % "5.1.41",
  "org.playframework.anorm" %% "anorm" % "2.6.0",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "joda-time" % "joda-time" % "2.10",
  "org.joda" % "joda-convert" % "2.1.1",
  "com.typesafe.play" %% "play-json-joda" % "2.7.2",
  "com.typesafe.akka" %% "akka-actor" % "2.5.22",
  "com.pauldijou" %% "jwt-core" % "2.1.0"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      