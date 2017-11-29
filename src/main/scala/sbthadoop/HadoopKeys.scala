package sbthadoop

import sbt._
import sbt.Keys.Classpath
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.conf.Configuration

object HadoopKeys {
  val hadoopClasspath = taskKey[Classpath]("Resources that will be used to configure the connection to Hadoop")
  val hadoopClasspathFromExecutable = taskKey[Classpath]("The classpath used by the given `hadoopExecutable`")
  val hadoopHdfs = taskKey[FileSystem]("The HDFS instance")
  val hadoopHdfsArtifactPath = taskKey[Path]("The HDFS path where an artifact will be published")
  val hadoopLocalArtifactPath = taskKey[File]("The location of the local resource that will be published to HDFS")
  val hadoopConfiguration = taskKey[Configuration]("A prepared Hadoop Configuration instance used to access HDFS")
  val hadoopUser = settingKey[String]("Username that will be used when interacting with Hadoop/HDFS")
  val hadoopExecutable = settingKey[Option[File]]("File system location of a local hadoop executable")
}
