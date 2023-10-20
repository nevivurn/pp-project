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
  def calculator(e: Expr): Long =
    e match
      case Num(n) => n
      case Add(f1, f2) =>
        calculator(f1) + calculator(f2)
      case Sub(f1, f2) =>
        calculator(f1) - calculator(f2)
      case Mul(f1, f2) =>
        calculator(f1) * calculator(f2)
      case Div(f1, f2) =>
        calculator(f1) / calculator(f2)

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
  def consumePeg(p: PEG, s: String): IOption[String] =
    p match
      case Str(s1) =>
        if s.startsWith(s1)
          then ISome(s.stripPrefix(s1))
          else INone
      case Cat(p1, p2) => consumePeg(p1, s) match {
        case ISome(s2) => consumePeg(p2, s2)
        case INone => INone
      }
      case OrdChoice(p1, p2) => consumePeg(p1, s) match {
        case ISome(s1) => ISome(s1)
        case INone => consumePeg(p2, s)
      }
      case NoneOrMany(p1) => consumePeg(p1, s) match {
        case ISome(s1) => consumePeg(p, s1)
        case INone => ISome(s)
      }
      case OneOrMany(p1) => consumePeg(p1, s) match {
        case ISome(s1) => consumePeg(NoneOrMany(p1), s1)
        case INone => INone
      }
      case Optional(p1) => consumePeg(p1, s) match {
        case ISome(s1) => ISome(s1)
        case INone => ISome(s)
      }
      case ExistP(p1) => consumePeg(p1, s) match {
        case ISome(s1) => ISome(s)
        case INone => INone
      }
      case NotP(p1) => consumePeg(p1, s) match {
        case ISome(s1) => INone
        case INone => ISome(s)
      }

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
  def matchPeg(p: PEG, s: String): Boolean =
    consumePeg(p, s) match
      case ISome("") => true
      case _ => false

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
  def mapPeg[V](pf: PEGFunc[V], s: String): IOption[V] =
    def toPeg(pf: PEGFunc[_]): PEG =
      pf match
        case FStr(s, _) => Str(s)
        case FCat(pf1, pf2, _) => Cat(toPeg(pf1), toPeg(pf2))
        case FOrdChoice(pf1, pf2, _) => OrdChoice(toPeg(pf1), toPeg(pf2))
        case FNoneOrMany(pf, _) => NoneOrMany(toPeg(pf))
        case FOneOrMany(pf, _) => OneOrMany(toPeg(pf))
        case FOptional(pf, _) => Optional(toPeg(pf))
        case FExistP(pf) => ExistP(toPeg(pf))
        case FNotP(pf) => NotP(toPeg(pf))
    val p = toPeg(pf)

    def get[V](o: IOption[V]) =
      o match
        case ISome(v) => v
        case INone => throw new Exception

    if !matchPeg(p, s)
      then INone
    else

    pf match
      case FStr(_, f) => ISome(f(s))
      case FCat(pf1, pf2, f) =>
        val p1 = toPeg(pf1)
        val p2 = toPeg(pf2)
        val s2 = get(consumePeg(p1, s))
        val s1 = s.stripSuffix(s2)
        ISome(f(get(mapPeg(pf1, s1)), get(mapPeg(pf2, s2))))
      case FOrdChoice(pf1, pf2, f) =>
        if matchPeg(toPeg(pf1), s)
          then ISome(f(ILeft(get(mapPeg(pf1, s)))))
          else ISome(f(IRight(get(mapPeg(pf2, s)))))
      case FNoneOrMany(pf1, f) =>
        val p1 = toPeg(pf1)
        consumePeg(p1, s) match {
          case ISome(srem) =>
            val s1 = s.stripSuffix(srem)
            val a1 = get(mapPeg(pf1, s1))
            val arem = get(mapPeg(FNoneOrMany(pf1, a => a), srem))
            ISome(f(ICons(a1, arem)))
          case INone => ISome(f(INil))
        }
      case FOneOrMany(pf1, f) =>
        val p1 = toPeg(pf1)
        val srem = get(consumePeg(p1, s))
        val s1 = s.stripSuffix(srem)
        val a1 = get(mapPeg(pf1, s1))
        val arem = get(mapPeg(FNoneOrMany(pf1, a => a), srem))
        ISome(f(ICons(a1, arem)))
      case FOptional(pf1, f) =>
        ISome(f(mapPeg(pf1, s)))
      case FExistP(pf1) =>
        if matchPeg(toPeg(pf1), s)
          then ISome(())
          else INone
      case FNotP(pf1) =>
        if !matchPeg(toPeg(pf1), s)
          then ISome(())
          else INone

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
  val arithPeg: PEGFunc[Expr] =
    def either[V](a: PEGFunc[V], b: PEGFunc[V]): PEGFunc[V] =
      FOrdChoice(a, b, (m) => m match {
        case ILeft(a) => a
        case IRight(a) => a
      })
    def oneOf[V](pfs: ICons[PEGFunc[V]]): PEGFunc[V] =
      pfs.tl match {
        case ICons(tlhd, tltl) => either(pfs.hd, oneOf(ICons(tlhd, tltl)))
        case INil => pfs.hd
      }

    val digit: PEGFunc[Num] = oneOf(
      ICons(FStr("0", _ => Num(0)),
        ICons(FStr("1", _ => Num(1)),
          ICons(FStr("2", _ => Num(2)),
            ICons(FStr("3", _ => Num(3)),
              ICons(FStr("4", _ => Num(4)),
                ICons(FStr("5", _ => Num(5)),
                  ICons(FStr("6", _ => Num(6)),
                    ICons(FStr("7", _ => Num(7)),
                      ICons(FStr("8", _ => Num(8)),
                        ICons(FStr("9", _ => Num(9)), INil)))))))))))
    val number = FOneOrMany(
      digit,
      nl => {
        def cons(nl: ICons[Num]): Long =
          nl.tl match {
            case ICons(hd, tl) => nl.hd.n *10 + cons(ICons(hd, tl))
            case INil => nl.hd.n
          }
        nl match {
          case ICons(hd, tl) => Num(cons(ICons(hd, tl)))
          case INil => throw new Exception // should never happen!
        }
      })

    val term = FCat(
      number,
      FNoneOrMany(
        FCat(
          either(FStr("*", _ => "*"), FStr("/", _ => "/")),
          number,
          (a, b) => (a, b)),
        m => m),
      (n, rest) => {
        def cons(n: Expr, nl: IList[(String, Expr)]): Expr =
          nl match {
            case ICons(hd, tl) =>
              if hd(0) == "*"
              then cons(Mul(n, hd(1)), tl)
              else cons(Div(n, hd(1)), tl)
            case INil => n
          }
        cons(n, rest)
      })

    val expr = FCat(
      term,
      FNoneOrMany(
        FCat(
          either(FStr("+", _ => "+"), FStr("-", _ => "-")),
          term,
          (a, b) => (a, b)),
        m => m),
      (n, rest) => {
        def cons(n: Expr, nl: IList[(String, Expr)]): Expr =
          nl match {
            case ICons(hd, tl) =>
              if hd(0) == "+"
              then cons(Add(n, hd(1)), tl)
              else cons(Sub(n, hd(1)), tl)
            case INil => n
          }
        cons(n, rest)
      })

    expr
