import Dependencies._
import Publishing._

sbtPlugin := true

crossSbtVersions := Seq("0.13.16", "1.0.0")

name := "sbt-hadoop"

organization := "com.tapad.sbt"

licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause"))

scalacOptions ++= Seq("-deprecation", "-language:_")

libraryDependencies ++= Seq(
  "org.scalatest"      %% "scalatest"         % ScalaTestVersion  % "test",
  "org.slf4j"           % "slf4j-api"         % SLF4JVersion,
  "org.slf4j"           % "log4j-over-slf4j"  % SLF4JVersion,
  "com.tapad.sbt"      %% "cli-util"          % "0.1.4",
  "org.apache.hadoop"   % "hadoop-client"     % HadoopVersion
    excludeAll(
      ExclusionRule(organization = "org.slf4j"),
      ExclusionRule(organization = "log4j")
    )
)

scriptedSettings

scriptedLaunchOpts ++= Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)

scriptedBufferLog := false

PublishSettings

ReleaseSettings
