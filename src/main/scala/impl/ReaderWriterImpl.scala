package pp202302.project.impl

import pp202302.project.common._

import IOType._

given Reader[IOType] with
  extension (r: IOType)
    // Hint: Use scala.Console.in.read().toChar
    def readChar(): Option[Char] =
      r match {
        case StdIO => Some(scala.Console.in.read().toChar)
        case rx: DummyIO =>
          if rx.input.length > 0 then
            val c = rx.input.charAt(0)
            rx.input = rx.input.substring(1)
            Some(c)
          else None
      }

given Writer[IOType] with
  extension (w: IOType)
    def writeChar(c: Char): Unit =
      w match {
        case StdIO => scala.Console.out.append(c)
        case rx: DummyIO => rx.output += c
      }
