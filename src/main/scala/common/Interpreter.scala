package pp202302.project.common

import scala.util.Try

trait Interpreter[E, V]:
  extension (e: E)
    def interpret[R, W](reader: R, writer: W)(using
        showOps: Show[V],
        readOps: Reader[R],
        writeOps: Writer[W]
    ): Try[V]
