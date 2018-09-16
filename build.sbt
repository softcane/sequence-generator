import sbt.Keys._
import play.sbt.PlayFilters

name := """sequence-generator"""

organization in ThisBuild := "com.softcane"

scalaVersion in ThisBuild :=  "2.12.6"

version := "1.0-SNAPSHOT"

val `play-json` = Seq(
  libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.9"
)

val phantom = Seq(
  libraryDependencies += "com.outworkers" %% "phantom-dsl" % "2.24.10"
)

val serviceSettings = phantom ++ Seq(
  libraryDependencies ++= Seq(
    guice,
    specs2 % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
  ),
  routesGenerator := InjectedRoutesGenerator
)

lazy val service = Project(
  id = "sequence-generator-service",
  base = file("service")
).settings(serviceSettings)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin, PlayFilters)
  .dependsOn(`models-scala`)

lazy val `models-scala` = Project(
  id = "sequence-generator-models-scala",
  base = file("models-scala")
).settings(`play-json`)

val root = Project(
  id = "sequence-generator",
  base = file(".")
).aggregate(List(service).map(p => p: ProjectReference):_*)
  .settings(run in Compile := (run in Compile in service).evaluated)

fork in run := true