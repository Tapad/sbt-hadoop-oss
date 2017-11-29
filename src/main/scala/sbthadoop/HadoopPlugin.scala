package sbthadoop

import sbt._
import sbt.Keys._
import scala.sys.process.{Process, ProcessLogger}

object HadoopPlugin extends AutoPlugin {

  object autoImport {
    type HdfsPath = org.apache.hadoop.fs.Path
    val Hadoop = config("hadoop").extend(Compile)
    val HadoopKeys = sbthadoop.HadoopKeys
    val HadoopUtils = sbthadoop.HadoopUtils
    val hadoopClasspath = HadoopKeys.hadoopClasspath
    val hadoopClasspathFromExecutable = HadoopKeys.hadoopClasspathFromExecutable
    val hadoopExecutable = HadoopKeys.hadoopExecutable
    val hadoopHdfs = HadoopKeys.hadoopHdfs
    val hadoopHdfsArtifactPath  = HadoopKeys.hadoopHdfsArtifactPath
    val hadoopLocalArtifactPath = HadoopKeys.hadoopLocalArtifactPath
    val hadoopConfiguration = HadoopKeys.hadoopConfiguration
    val hadoopUser = HadoopKeys.hadoopUser
  }

  import autoImport._, HadoopUtils._

  override def projectSettings = defaultSettings ++ requiredSettings ++ inConfig(Hadoop)(scopedSettings)

  lazy val defaultSettings = Seq(
    hadoopClasspath := Seq.empty,
    hadoopClasspathFromExecutable := {
      hadoopExecutable.value match {
        case None => sys.error("`hadoopExecutable` must be defined in order to export its classpath")
        case Some(executable) =>
          try {
            val process = Process(s"${executable.getAbsolutePath} classpath")
            val result = process.!!.trim
            val paths = IO.parseClasspath(result)
            Attributed.blankSeq(paths)
          } catch {
            case e: Exception =>
              sys.error("Could not export classpath from `hadoopExecutable`: " + ErrorHandling.reducedToString(e))
          }
      }
    },
    hadoopExecutable := sys.env.get("HADOOP_HOME").map(file(_) / "bin" / "hadoop"),
    hadoopHdfs := {
      val configuration = hadoopConfiguration.value
      val username = hadoopUser.value
      getFileSystem(configuration, username)
    },
    hadoopLocalArtifactPath := {
      (artifactPath in (Compile, packageBin)).value
    },
    hadoopConfiguration := {
      val log = streams.value.log
      val classpathFiles = hadoopClasspath.value.files.flatMap {
        case path if path.exists && path.isFile => Seq(path)
        case path if path.exists && path.isDirectory => filesInDirectory(path)
        case path if path.getName == "*" => filesInDirectory(path.getParentFile)
        case path => // entry can not easily be (1) used as or (2) expanded into an absolute, local resource path
          log.debug(s"Skipping `hadoopClasspath` entry $path for FileSystem configuration")
          Seq.empty
      }
      getConfiguration(classpathFiles)
    },
    hadoopUser := {
      val process = Process("whoami")
      lazy val output = process.!!
      sys.props.getOrElse("user.name", output)
    }
  )

  lazy val requiredSettings = Seq(
    hadoopHdfsArtifactPath := {
      sys.error("The HDFS artifact path is not defined. Please declare a value for the `hadoopHdfsArtifactPath` setting.")
    }
  )

  lazy val scopedSettings = Seq(
    packageBin := {
      (packageBin in Compile).value
    },
    publish := {
      val _ = (packageBin in Hadoop).value
      val localPath = hadoopLocalArtifactPath.value
      val remotePath = hadoopHdfsArtifactPath.value
      val hdfs = hadoopHdfs.value
      val log = streams.value.log
      try {
        log.info(s"Copying $localPath to $remotePath")
        hdfs.copyFromLocalFile(false, true, localPath, remotePath)
      } catch {
        case e: Exception =>
          sys.error(s"Could not publish $localPath to $remotePath: " + ErrorHandling.reducedToString(e))
      }
    }
  )
}
