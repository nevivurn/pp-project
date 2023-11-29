package pp202302.project.impl

import pp202302.project.common._

import IOType._

given Reader[IOType] with
  extension (r: IOType)
    def readChar(): Char = ???

given Writer[IOType] with
  extension (w: IOType)
    def writeChar(c: Char): Unit = ???
