package sbthadoop

import java.io.File
import sbt.PathFinder

object PathFinderUtils {

  def getAllPaths(dir: File): Seq[File] = {
    PathFinder(dir).allPaths.get
  }
}
