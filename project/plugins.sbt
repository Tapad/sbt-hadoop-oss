addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.4")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
