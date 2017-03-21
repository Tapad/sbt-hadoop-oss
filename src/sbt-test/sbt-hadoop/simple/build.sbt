name := "hadoop-simple"

organization := "com.tapad.sbt"

version := "0.1.0"

hadoopHdfsArtifactPath := new HdfsPath(s"/tmp/${name.value}-${version.value}.jar")

enablePlugins(HadoopPlugin)
