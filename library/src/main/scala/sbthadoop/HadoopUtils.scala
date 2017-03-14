package sbthadoop

import java.io.File
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

object HadoopUtils {

  val HadoopConfigurationFilenames = Set("core-site.xml", "hdfs-site.xml")

  def getFileSystem(configurationFiles: Seq[File], username: String): FileSystem = {
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
      val uri = FileSystem.getDefaultUri(configuration)
      FileSystem.newInstance(uri, configuration, username)
    } catch {
      case e: Exception => sys.error(s"Error occurred when trying to create instance of FileSystem: ${e.getMessage}")
    }
  }
}
