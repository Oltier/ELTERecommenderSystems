package hu.elte.inf.recommenderSystems.utils

import java.nio.file.{FileSystems, Paths}

object DirectoryPathUtil {

  def getResourceDirectoryPath(dirName: String): String = {
    val uri = getClass.getResource(dirName).toURI
    if(uri.getScheme.equals("jar")){
      val fileSystem = FileSystems.newFileSystem(uri, new java.util.HashMap[String, Object])
      fileSystem.getPath(dirName).toString
    } else {
      Paths.get(uri).toString
    }
  }

}
