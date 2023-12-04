package pp202302.project

import org.parboiled2.{ErrorFormatter, ParseError}
import pp202302.project.common.{_, given}
import pp202302.project.impl.given

import scala.annotation.tailrec
import scala.util.{Failure, Success}

object ExprRepl:
  val ioChannel = IOType.StdIO

  @tailrec
  def run(): Unit =
    print("Input your program [Enter for exit]:\n > ")

    val input = scala.io.StdIn.readLine()
    if input == "" then
      println("Goodbye!")
      return

    val parser = new ExprParser(input)
    val result = parser.Input.run()

    val message = result match
      case Success(e) =>
        e.interpret(ioChannel, ioChannel) match
          case Success(v) => s"Result: ${v.show}"
          case Failure(e) =>
            e.printStackTrace()
            s"Error: ${e.getMessage}"
      case Failure(e: ParseError) =>
        s"Parse Error: ${parser.formatError(e, new ErrorFormatter(showTraces = true))}"
      case Failure(e) =>
        s"Error: ${e.getMessage}"

    println(message)

    run()
