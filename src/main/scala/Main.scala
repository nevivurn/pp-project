package pp202302.assign2

import scala.annotation.tailrec
import scala.util.control.TailCalls._

/** Principles of Programming: Assignment 02.
  *
  * Implement given functions, which are currently left blank. (???) **WARNING:
  * Please read the restrictions below carefully.**
  *
  * If you do not follow these, **your submission will not be graded.**
  *
  *   - Do not use the keyword `var`. Use `val` and `def` instead.
  *   - Do not use any library functions or data structures like `List`,
  *     `Array`, `Range` (`1 to n`, `1 until n` ...), `fold`, `map`, `reduce` or
  *     etc.
  *   - If you want to use a data structure, create new one instead of using the
  *     library ones.
  *   - You can only use tuples, `scala.annotation.tailrec`, and
  *     `scala.util.control.TailCalls._`, `Math._` from the library.
  *   - Do not use any looping syntax of Scala (`for`, `while`, `do-while`,
  *     `yield`, ...)
  *
  * Again, your score will be zero if you do not follow these rules.
  *
  * Note that these rules will be gradually relaxed through the next
  * assignments.
  *
  * We do not require tail-recursion explicitly for this assignment.
  *
  * Timeout: 30 sec.
  */
object Assignment2:
  import Expr.*
  import PEG.*
  import PEGFunc.*
  import IOption.*
  import IEither.*
  import IList.*

  /** Problem 1: Simple Arithmetic Calculator (10 Points)
    */
  def calculator(e: Expr): Long = ???

  /** Problem 2: Parsing Expression Grammar.
    *
    * Parsing Expression Grammar (PEG) is one way to describe a grammar of
    * language, especially for programming languages.
    *
    * Starting from Python 3.9, Python uses PEG to describe its grammar and its
    * parsing speed was increased by 10%.
    *
    * Unlike CFG (Context-Free Grammar), all elements in PEG are
    * **deterministic** and **greedy**, so there is no ambiguity on parsing.
    *
    * See https://en.wikipedia.org/wiki/Parsing_expression_grammar
    */

  /** Problem 2-1: Consume PEG (30 points)
    *
    * Match prefix of the string `s` and return the remained suffix.
    *
    * ```
    * // e.g.)
    * consumePeg("ab" ~ "c"*, "abcccccdccc") == ISome("dccc")
    * consumePeg(!"a" ~ "bc", "bcd") == ISome("d")
    * consumePeg(!"a" ~ "bc", "abc") == INone
    * ```
    */
  def consumePeg(p: PEG, s: String): IOption[String] = ???

  /** Problem 2-2: Match PEG (10 points)
    *  
    * Return true if the given string `s` matches with the given PEG `p`.
    * 
    * ```
    * // e.g.)
    * matchPeg("abc", "abcd") == false
    * matchPeg("a"+, "aaaa") == true
    * ```
    */
  def matchPeg(p: PEG, s: String): Boolean = ???

  /** Problem 2-3: Map PEG to value (25 points)
    * 
    * `PEGFunc[V]` is a PEG expression with expression mapper which returns a value 
    * with the type `V`.
    * 
    * e.g.) `FStr(s, f)` matches a string `s` and returns `f(s)`.
    * 
    * If `pf` matches with `s`, return `ISome(v)`, where `v` is the value
    * returned by internal function of `pf`. Otherwise, return `INone`.
    */
  def mapPeg[V](pf: PEGFunc[V], s: String): IOption[V] = ???

  /** Problem 2-4: Arithmetic PEG (15 points)
    *
    * Write a PEG expression which extracts an arithmetic expression.
    * We allow leading 0s in the expressions, and all the numbers will be in the range of Long.
    * 
    * PEG expression of arithmetic expression looks like below.
    * ```
    * Expr = MulDiv ~ ("+" ~ MulDiv | "-" ~ MulDiv)*
    * MulDiv = Num ~ ("*" ~ Num | "/" ~ Num)*
    * Num = (0-9)+
    * ```
    */
  val arithPeg: PEGFunc[Expr] = ???