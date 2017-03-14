import Dependencies._
import Publishing._

/* The base, minimal settings for every project, including the root aggregate project */
val BaseSettings = Seq(
  organization := "com.tapad.sbt",
  licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause")),
  scalaVersion := Dependencies.ScalaVersion
)

/* Common settings for all non-aggregate subprojects */
val CommonSettings = BaseSettings ++ Seq(
  scalacOptions ++= Seq("-deprecation", "-language:_"),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % ScalaTestVersion % "test"
  )
)

val PluginSettings = CommonSettings ++ scriptedSettings ++ Seq(
  sbtPlugin := true,
  scriptedLaunchOpts ++= Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value),
  scriptedBufferLog := false
)

val ExcludedLoggingFrameworks = Seq("org.slf4j" -> "slf4j-log4j12", "log4j" -> "log4j")

lazy val root = (project in file("."))
  .settings(BaseSettings: _*)
  .settings(NoopPublishSettings: _*)
  .settings(ReleaseSettings: _*)
  .aggregate(plugin, library)
  .enablePlugins(CrossPerProjectPlugin)

lazy val plugin = (project in file("plugin"))
  .settings(PluginSettings: _*)
  .settings(PluginPublishSettings: _*)
  .settings(
    name := "sbt-hadoop",
    libraryDependencies ++= Seq(
      "org.slf4j"          % "log4j-over-slf4j" % "1.7.24",
      "org.slf4j"          % "slf4j-jdk14"      % "1.7.24" % "test",
      "org.scalactic"     %% "scalactic"        % ScalacticVersion,
      "com.tapad.sbt"     %% "cli-util"         % "0.1.0-SNAPSHOT"
    ),
    publishLocal := {
      (publishLocal.dependsOn(publishLocal in library)).value
    }
  )
  .dependsOn(library)

lazy val library = (project in file("library"))
  .settings(CommonSettings: _*)
  .settings(LibraryPublishSettings: _*)
  .settings(
    name := "hadoop-util",
    libraryDependencies ++= Seq(
      "org.slf4j"          % "slf4j-api"      % "1.7.24",
      "org.apache.hadoop"  % "hadoop-client"  % "2.7.3"
    ),
    libraryDependencies := libraryDependencies.value.map {
      ExcludedLoggingFrameworks.foldLeft(_) {
        case (dependency, (group, artifact)) => dependency.exclude(group, artifact)
      }
    }
  )
