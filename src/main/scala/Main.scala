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
    def mapp[V](pf: PEGFunc[V], s: String): IOption[(V, String)] =
      pf match {
        case FStr(sm, f) if s.startsWith(sm) =>
          ISome((f(sm), s.stripPrefix(sm)))
        case FStr(_, _) => INone
        case FCat(p1, p2, f) =>
          mapp(p1, s) match {
            case ISome((v1, srem)) =>
              mapp(p2, srem) match {
                case ISome((v2, srest)) => ISome(f(v1, v2), srest)
                case INone => INone
              }
            case INone => INone
          }
        case FOrdChoice(p1, p2, f) =>
          mapp(p1, s) match {
            case ISome((v, srem)) => ISome(f(ILeft(v)), srem)
            case INone =>
              mapp(p2, s) match {
                case ISome((v, srem)) => ISome(f(IRight(v)), srem)
                case INone => INone
              }
          }
        case FNoneOrMany(p1, f) =>
          mapp(p1, s) match {
            case ISome((v, srem)) =>
              mapp(FNoneOrMany(p1, v => v), srem) match {
                case ISome((vrem, srest)) =>
                  ISome(f(ICons(v, vrem)), srest)
                case INone => throw new Exception // never happens
              }
            case INone => ISome(f(INil), s)
          }
        case FOneOrMany(p1, f) =>
          mapp(p1, s) match {
            case ISome((v, srem)) =>
              mapp(FNoneOrMany(p1, v => v), srem) match {
                case ISome((vrem, srest)) =>
                  ISome(f(ICons(v, vrem)), srest)
                case INone => throw new Exception // never happens
              }
            case INone => INone
          }
        case FOptional(p1, f) =>
          mapp(p1, s) match {
            case ISome(v, srem) => ISome((f(ISome(v)), srem))
            case INone => ISome((f(INone), s))
          }
        case FExistP(p1) =>
          mapp(p1, s) match {
            case ISome(v, _) => ISome(((), s))
            case INone => INone
          }
        case FNotP(p1) =>
          mapp(p1, s) match {
            case ISome(v, _) => INone
            case INone => ISome(((), s))
          }
      }

    mapp(pf, s) match {
      case ISome(v, s) if s.isEmpty() => ISome(v)
      case _ => INone
    }

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
        def cons(nl: IList[Num], acc: Long): Long =
          nl match {
            case ICons(hd, tl) => cons(tl, acc*10 + hd.n)
            case INil => acc
          }
        nl match {
          case ICons(hd, tl) => Num(cons(ICons(hd, tl), 0))
          case INil => throw new Exception("empty integer?!") // should never happen!
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
