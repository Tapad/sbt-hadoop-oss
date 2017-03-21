name := "hadoop-executable-classpath"

organization := "com.tapad.sbt"

version := "0.1.0"

hadoopHdfsArtifactPath := new HdfsPath(s"/tmp/${name.value}-${version.value}.jar")

hadoopExecutable := Some((resourceDirectory in Compile).value / "hadoop" / "bin" / "hadoop")

hadoopClasspath := hadoopClasspathFromExecutable.value

enablePlugins(HadoopPlugin)
