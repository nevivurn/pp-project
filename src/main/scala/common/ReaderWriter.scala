package pp202302.project.common

trait Reader[-R]:
  // Read a character from the reader.
  // If the reader is empty, return None.
  extension (r: R) def readChar(): Option[Char]

trait Writer[-W]:
  extension (w: W) def writeChar(c: Char): Unit
