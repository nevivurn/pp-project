/// !!!DO NOT FIX THIS FILE!!!  If you fix this file, your code will not be compiled.

package pp202302.assign2

/** Note: enum is new feature of Scala 3 which alters `sealed abstract class`
  * to define case classes and abstract data types.
  *
  * See https://docs.scala-lang.org/scala3/reference/enums/enums.html
  *
  * e.g.)
  * ```
  * abstract class Expr
  * case class Number(n: Long) extends Expr
  * ```
  *
  * is same as
  *
  * ```
  * enum Expr:
  *   case Number(n: Long)
  * ```
  */

/// =========================================
/// ===== !!!DO NOT FIX THIS FILE!!! ========
/// =========================================

// Simple Arithmetic Expression
enum Expr:
  // single number
  case Num(n: Long)
  // f1 + f2
  case Add(f1: Expr, f2: Expr)
  // f1 - f2
  case Sub(f1: Expr, f2: Expr)
  // f1 & f2
  case Mul(f1: Expr, f2: Expr)
  // f1 / f2 (use regular scala division)
  case Div(f1: Expr, f2: Expr)

/** Parsing Expression Grammar
  *
  * See https://en.wikipedia.org/wiki/Parsing_expression_grammar
  */
enum PEG:
  // s: Match this string
  case Str(s: String)
  // p1 ~ p2: Match two patterns in sequence
  case Cat(p1: PEG, p2: PEG)
  // p1 / p2: Match first. If the first fails, try the second.
  case OrdChoice(p1: PEG, p2: PEG)
  // p* : GREEDLY Match zero or more repetitions of the pattern
  // (i.e. consuming as much input as possible and never backtracking)
  // e.g.) Cat(a*, a) will always fail because the first a* will consume all the 'a's.
  case NoneOrMany(p: PEG)
  // p+ : GREEDLY Match one or more repetitions of the pattern
  // (i.e. consuming as much input as possible and never backtracking)
  case OneOrMany(p: PEG)
  // p? : Match zero or one time of the pattern
  case Optional(p: PEG)
  // &p : Match p, but do not consume the input
  case ExistP(p: PEG)
  // !p : Match anything but p, but do not consume the input
  case NotP(p: PEG)

enum IOption[+A]:
  case ISome(a: A)
  case INone

enum IList[+A]:
  case ICons(hd: A, tl: IList[A])
  case INil

enum IEither[A, B]:
  case ILeft(a: A)
  case IRight(b: B)

// PEG with mapper function
enum PEGFunc[V]:
  case FStr(s: String, f: String => V)
  case FCat[A, B, C](p1: PEGFunc[A], p2: PEGFunc[B], f: (A, B) => C)
      extends PEGFunc[C]
  case FOrdChoice[A, B, C](
      p1: PEGFunc[A],
      p2: PEGFunc[B],
      f: IEither[A, B] => C
  ) extends PEGFunc[C]
  case FNoneOrMany[A, B](p: PEGFunc[A], f: IList[A] => B) extends PEGFunc[B]
  case FOneOrMany[A, B](p: PEGFunc[A], f: IList[A] => B) extends PEGFunc[B]
  case FOptional[A, B](p: PEGFunc[A], f: IOption[A] => B) extends PEGFunc[B]
  case FExistP[A](p: PEGFunc[A]) extends PEGFunc[Unit]
  case FNotP[A](p: PEGFunc[A]) extends PEGFunc[Unit]
