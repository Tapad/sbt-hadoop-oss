package sbthadoop

import sbt._
import sbt.Keys._

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
            val result = s"${executable.getAbsolutePath} classpath".!!.trim
            val files = IO.parseClasspath(result)
            Attributed.blankSeq(files)
          } catch {
            case e: Exception =>
              sys.error("Could not export classpath from `hadoopExecutable`: " + ErrorHandling.reducedToString(e))
          }
      }
    },
    hadoopExecutable := sys.env.get("HADOOP_HOME").map(file(_) / "bin" / "hadoop"),
    hadoopHdfs := {
      val configurationFiles = hadoopClasspath.value.files
      val username = hadoopUser.value
      getFileSystem(configurationFiles, username)
    },
    hadoopLocalArtifactPath := {
      (artifactPath in (Compile, packageBin)).value
    },
    hadoopUser := sys.props.getOrElse("user.name", "whoami".!!)
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
      try {
        hdfs.copyFromLocalFile(false, true, localPath, remotePath)
      } catch {
        case e: Exception =>
          sys.error(s"Could not publish $localPath to $remotePath: " + ErrorHandling.reducedToString(e))
      }
    }
  )
}
