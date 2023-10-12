// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
import pp202302.assign2.Assignment2.{
  calculator,
  consumePeg,
  matchPeg,
  mapPeg,
  arithPeg
}
import pp202302.assign2.{Expr, PEG, PEGFunc, IOption, IEither, IList}

class TestSuite extends munit.FunSuite {
  import Expr.*
  import PEG.*
  import PEGFunc.*
  import IOption.*
  import IEither.*
  import IList.*

  test("problem 1") {
    assertEquals(
      calculator(Num(3)),
      3L
    )
    assertEquals(
      calculator(Add(Num(3), Num(4))),
      7L
    )
    assertEquals(
      calculator(Sub(Num(1), Num(5))),
      -4L
    )
    assertEquals(
      calculator(Mul(Num(5), Num(3))),
      15L
    )
    assertEquals(
      calculator(Div(Num(10), Num(3))),
      3L
    )
    assertEquals(
      calculator(Mul(Add(Num(3), Num(2)), Num(6))),
      30L
    )
  }

  test("problem 2-1-1") {
    assertEquals(
      consumePeg(Str("a"), "abc"),
      ISome("bc")
    )
    assertEquals(
      consumePeg(Str("a"), "bcd"),
      INone
    )
    assertEquals(
      consumePeg(Str("abc"), "abcdef"),
      ISome("def")
    )
    assertEquals(
      consumePeg(Str("abc"), "abdef"),
      INone
    )
    assertEquals(
      consumePeg(Cat(Str("a"), Str("b")), "abc"),
      ISome("c")
    )
    assertEquals(
      consumePeg(Cat(Str("a"), Str("d")), "abd"),
      INone
    )
    assertEquals(
      consumePeg(OrdChoice(Str("a"), Str("b")), "abc"),
      ISome("bc")
    )
    assertEquals(
      consumePeg(OrdChoice(Str("a"), Str("b")), "bcd"),
      ISome("cd")
    )
    assertEquals(
      consumePeg(OrdChoice(Str("a"), Cat(Str("a"), Str("a"))), "aaa"),
      ISome("aa")
    )
    assertEquals(
      consumePeg(NoneOrMany(Str("a")), "aaa"),
      ISome("")
    )
    assertEquals(
      consumePeg(NoneOrMany(Str("a")), "bcd"),
      ISome("bcd")
    )
    assertEquals(
      consumePeg(NoneOrMany(OrdChoice(Str("a"), Str("ab"))), "aaabaaa"),
      ISome("baaa")
    )
    assertEquals(
      consumePeg(OneOrMany(Str("a")), "aaa"),
      ISome("")
    )
    assertEquals(
      consumePeg(OneOrMany(Str("a")), "bcd"),
      INone
    )
    assertEquals(
      consumePeg(Optional(Str("a")), "aaa"),
      ISome("aa")
    )
    assertEquals(
      consumePeg(Optional(Str("a")), "bcd"),
      ISome("bcd")
    )
    assertEquals(
      consumePeg(ExistP(Str("a")), "abc"),
      ISome("abc")
    )
    assertEquals(
      consumePeg(ExistP(Str("a")), "bcd"),
      INone
    )
    assertEquals(
      consumePeg(
        Cat(
          Cat(
            Str("("),
            NoneOrMany(Cat(NotP(Str(")")), OrdChoice(Str("a"), Str("b"))))
          ),
          Str(")")
        ),
        "(ababaaab)ababa"
      ),
      ISome("ababa")
    )
    assertEquals(
      consumePeg(NotP(Str("a")), "abc"),
      INone
    )
    assertEquals(
      consumePeg(NotP(Str("a")), "bcd"),
      ISome("bcd")
    )
  }

  test("problem 2-2") {
    assertEquals(
      matchPeg(NoneOrMany(OrdChoice(Str("a"), Str("ab"))), "aaabaaa"),
      false
    )
    assertEquals(
      matchPeg(NoneOrMany(OrdChoice(Str("a"), Str("ab"))), "aaab"),
      false
    )
    assertEquals(
      matchPeg(NoneOrMany(OrdChoice(Str("ab"), Str("a"))), "aaab"),
      true
    )
  }

  test("problem 2-3") {
    assertEquals(
      mapPeg(FStr("a", _.toUpperCase), "abc"): IOption[String],
      INone
    )
    assertEquals(
      mapPeg(FStr("1", _.toInt), "1"): IOption[Int],
      ISome(1)
    )
    assertEquals(
      mapPeg(
        FCat(
          FStr("1", _.toInt),
          FStr("2", _.toInt),
          _ * 10 + _
        ),
        "12"
      ): IOption[Int],
      ISome(12)
    )
    assertEquals(
      mapPeg(
        FOrdChoice(
          FStr("1", _.toInt),
          FStr("2", _.toInt),
          { // implicit matching function
            case ILeft(a)  => a * 3
            case IRight(b) => b * 5
          }
        ),
        "2"
      ): IOption[Int],
      ISome(10)
    )

    assertEquals(
      mapPeg(
        FNoneOrMany(
          FCat(
            FOptional(FStr("a", _.toUpperCase), x => x),
            FStr("b", _.toUpperCase),
            (x, y) => (x, y)
          ),
          x => x
        ),
        "abbab"
      ),
      ISome(
        ICons(
          (ISome("A"), "B"),
          ICons((INone, "B"), ICons((ISome("A"), "B"), INil))
        )
      )
    )

    def toRevNum(l: IList[Int]): Int = l match
      case INil           => 0
      case ICons(h, INil) => h
      case ICons(h, t)    => h + 10 * toRevNum(t)

    assertEquals(
      mapPeg(
        FNoneOrMany(
          FOrdChoice(
            FStr("3", _ => 3),
            FStr("5", _ => 5),
            {
              case ILeft(a)  => a
              case IRight(b) => b
            }
          ),
          toRevNum
        ),
        "35533"
      ): IOption[Int],
      ISome(33553)
    )
  }

  test("problem 2-4") {
    assertEquals(
      mapPeg(arithPeg, "1+2"): IOption[Expr],
      ISome(Add(Num(1), Num(2)))
    )
    assertEquals(
      calculator(
        mapPeg(arithPeg, "1+5+10/3*2") match {
          case ISome(e) => e
          case INone    => Num(0)
        }
      ),
      12L
    )
  }
}
