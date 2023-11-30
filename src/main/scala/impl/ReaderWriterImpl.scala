package pp202302.project.impl

import pp202302.project.common._

import IOType._

given Reader[IOType] with
  extension (r: IOType)
    // Hint: Use scala.Console.in.read().toChar
    def readChar(): Option[Char] = ???

given Writer[IOType] with
  extension (w: IOType)
    def writeChar(c: Char): Unit = ???
