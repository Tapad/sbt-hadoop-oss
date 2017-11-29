package sbthadoop

import java.io.File
import sbt.Attributed
import sbt.Keys.Classpath
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

object HadoopUtils {

  val HadoopConfigurationFilenames = Set("core-site.xml", "hdfs-site.xml")

  def getConfiguration(configurationFiles: Seq[File]): Configuration = {
    try {
      val configuration = new Configuration(false)
      for {
        file <- configurationFiles
        fileName = file.getName
        if HadoopConfigurationFilenames.contains(fileName)
        fileAbsolutePath = file.getAbsolutePath
        path = new Path(fileAbsolutePath)
      } {
        configuration.addResource(path)
      }
      configuration.setIfUnset("fs.file.impl", "org.apache.hadoop.fs.LocalFileSystem")
      configuration.setIfUnset("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem")
      configuration.setClassLoader(getClass.getClassLoader)
      configuration
    } catch {
      case e: Exception => sys.error(s"Error occurred when trying to create Configuration instance: ${e.getMessage}")
    }
  }

  def getFileSystem(configurationFiles: Seq[File], username: String): FileSystem = {
    val configuration = getConfiguration(configurationFiles)
    getFileSystem(configuration, username)
  }

  def getFileSystem(configuration: Configuration, username: String): FileSystem = {
    try {
      val uri = FileSystem.getDefaultUri(configuration)
      FileSystem.newInstance(uri, configuration, username)
    } catch {
      case e: Exception => sys.error(s"Error occurred when trying to create instance of FileSystem: ${e.getMessage}")
    }
  }

  def classpathFromDirectory(dir: File): Classpath = {
    Attributed.blankSeq(filesInDirectory(dir))
  }

  def filesInDirectory(dir: File): Seq[File] = {
    PathFinderUtils.getAllPaths(dir)
  }

  implicit def fileToPath(file: File): Path = {
    new Path(file.getAbsolutePath)
  }
}
