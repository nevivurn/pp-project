package pp202302.project.common

trait Reader[-R]:
  extension (r: R) def readChar(): Char

trait Writer[-W]:
  extension (w: W) def writeChar(c: Char): Unit
