name := "hadoop-static-classpath"

organization := "com.tapad.sbt"

version := "0.1.0"

hadoopHdfsArtifactPath := new HdfsPath(s"/tmp/${name.value}-${version.value}.jar")

hadoopClasspath := {
  val etc = (resourceDirectory in Compile).value / "hadoop" / "etc"
  HadoopUtils.classpathFromDirectory(etc)
}

enablePlugins(HadoopPlugin)
