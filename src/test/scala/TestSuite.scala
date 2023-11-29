// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html

import org.parboiled2.{ErrorFormatter, ParseError}
import pp202302.project.common.{_, given}
import pp202302.project.impl.{_, given}
import pp202302.project.impl.ExprInterpreter.{_, given}

import scala.annotation.tailrec
import scala.util.{Failure, Success}

class TestSuite extends munit.FunSuite {
  import Val._
  import Arg._
  import Bind._
  import Expr._
  import IOAction._

  def parse(str: String): Expr =
    val parser = new ExprParser(str)
    val result = parser.Input.run()
    result match
      case Success(v) => v
      case Failure(e: ParseError) =>
        throw new RuntimeException(
          parser.formatError(e, new ErrorFormatter(showTraces = true))
        )
      case Failure(e) =>
        e.printStackTrace()
        throw new RuntimeException(e)

  def exec(code: String, input: String): (Val, String) =
    val dummyIO: IOType.DummyIO = IOType.DummyIO(input, "")
    val result = parse(code).interpret(dummyIO, dummyIO).get.evaluate
    (result, dummyIO.output)

  def runAssert(code: String, expect: Val) =
    val (result, _) = exec(code, "")
    assertEquals(result, expect)

  def runAssertIO(code: String, expect: Val, input: String, output: String) =
    val (result, outputResult) = exec(code, input)
    assertEquals(result, expect)
    assertEquals(outputResult, output)

  test("Basic parse") {
    runAssert("3", VInt(3))
    runAssert("3.5", VFloat(3.5))
  }

  test("Basic let") {
    val adder =
      """
        |(let (val x 1) (val y 2) (+ x y))
        |""".stripMargin

    runAssert(adder, VInt(3))
  }

  // TODO: 11/30
}
