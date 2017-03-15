name := "hadoop-executable-classpath"

organization := "com.tapad.sbt"

version := "0.1.0"

hadoopHdfsArtifactPath := new HdfsPath(s"/tmp/${name.value}-${version.value}.jar")

hadoopExecutable := {
  sys.env.get("HADOOP_HOME") match {
    case None => sys.error("This project requires a local hadoop executable discoverable from $HADOOP_HOME")
    case _ => hadoopExecutable.value
  }
}

hadoopClasspath := hadoopClasspathFromExecutable.value

enablePlugins(HadoopPlugin)
