package pp202302.project.impl

import pp202302.project.common.{_, given}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object ExprInterpreter {
  import Arg._
  import Bind._
  import Expr._
  import IOAction._
  import Val._

  given exprInterpreter[Env, V](using
      envOps: EnvOps[Env, V],
      lazyOps: LazyOps[Val, V]
  ): Interpreter[Expr, V] with
    extension (e: Expr)
      def interpret[R, W](reader: R, writer: W)(using
          showOps: Show[V],
          readOps: Reader[R],
          writeOps: Writer[W]
      ): Try[V] = {
        
        def eval(env: Env, expr: Expr): V = ???

        try {
          Success(eval(envOps.emptyEnv(), e))
        } catch {
          case e: Exception => Failure(e)
        }
      }

}
