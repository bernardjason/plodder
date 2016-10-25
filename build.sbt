
name := """plodder"""

version := "1.0-SNAPSHOT"

import com.github.play2war.plugin._

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers += "soapui" at "http://www.soapui.org/repository/maven2"

libraryDependencies ++= Seq(
  filters
  ,"org.xerial" % "sqlite-jdbc" % "3.8.6"
  ,"com.typesafe.play" %% "play-slick" % "1.1.1"
  ,"com.typesafe.play" %% "play-slick-evolutions" % "1.1.1"
  ,"io.swagger" %% "swagger-play2" % "1.5.2"
  ,"org.webjars" % "swagger-ui" % "2.1.4"
  ,javaWs
  ,"org.scalatest" %% "scalatest" % "2.2.1" % "test"
  ,"org.scalatestplus" %% "play" % "1.4.0-M3" % "test"
  , ws
  , "com.smartbear.soapui" % "soapui-maven-plugin" % "5.1.0"
)

libraryDependencies += evolutions


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


