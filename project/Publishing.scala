import sbt._
import sbt.Keys._
import bintray.BintrayKeys._

object Publishing {

  val PublishSettings = Seq(
    autoAPIMappings := true,
    bintrayOrganization := Some("tapad-oss"),
    bintrayRepository := "sbt-plugins",
    pomIncludeRepository := { _ => false },
    publishArtifact in Test := false,
    publishArtifact in (Compile, packageDoc) := true,
    publishArtifact in (Compile, packageSrc) := true,
    homepage := Some(new URL("https://github.com/Tapad/sbt-hadoop")),
    pomExtra := {
      <developers>
        <developer>
          <id>jeffreyolchovy</id>
          <name>Jeffrey Olchovy</name>
          <email>jeffo@tapad.com</email>
          <url>https://github.com/jeffreyolchovy</url>
        </developer>
      </developers>
      <scm>
        <url>https://github.com/Tapad/sbt-hadoop</url>
        <connection>scm:git:git://github.com/Tapad/sbt-hadoop.git</connection>
      </scm>
    }
  )
}
