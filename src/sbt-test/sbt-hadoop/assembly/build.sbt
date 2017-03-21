name := "hadoop-assembly"

organization := "com.tapad.sbt"

version := "0.1.0"

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

hadoopLocalArtifactPath := (assemblyOutputPath in assembly).value

hadoopHdfsArtifactPath := new HdfsPath("/tmp", (assemblyJarName in assembly).value)

publish in Hadoop := (publish in Hadoop).dependsOn(assembly).value

enablePlugins(HadoopPlugin)
