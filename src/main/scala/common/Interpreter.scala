package pp202302.project.common

import scala.util.Try

trait Interpreter[E, V]:
  extension (e: E)
    // If the evaluation result of `e` is `VIOThunk`, just return it as is and do not execute IO.
    def evaluate(): Try[V]

    // If the return value of `e` is `VIOThunk`, execute all the IO sequences and return the result.
    def interpret[R, W](reader: R, writer: W)(using
        showOps: Show[V],
        readOps: Reader[R],
        writeOps: Writer[W]
    ): Try[V]
