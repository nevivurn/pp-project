package pp202302.project.common

import org.parboiled2._

import scala.annotation.switch

class ExprParser(val input: ParserInput) extends Parser {
  import Arg._
  import Expr._
  import Bind._
  import IOAction._

  def Input: Rule1[Expr] = rule {
    WL ~ SExpr ~ WL ~ EOI
  }

  def SExpr: Rule1[Expr] = rule {
    SELiteral | '(' ~ WL ~ SEParens ~ WL ~ ')'
  }

  def SExprList: Rule1[List[Expr]] = rule {
    (SExpr ~ (&(ch(')') | EOI) | SP)).* ~> ((el: Seq[Expr]) => el.toList)
  }

  def SELiteral: Rule1[Expr] = rule {
    (atomic("nil") ~ &(SP | ')') ~ push(ENil)) |
      (SEFloat ~> (EFloat(_))) |
      (SEInteger ~> (EInt(_))) |
      (SEString ~> (EString(_))) |
      (Ident ~> (EName(_)))
  }

  def SEParens: Rule1[Expr] = rule {
    run {
      (cursorChar: @switch) match {
        case 'c' => SECons
        case 'f' => SEFst | SEFloatP
        case 's' => SESnd | SEStringP | SESubstr
        case 'a' => SEApp
        case 'l' => SELet | SELen
        case 'n' => SENilP
        case 'p' => SEPairP
        case 'i' => SEIntP | SEIf
        case '+' | '-' | '*' | '=' | '<' | '>' | '/' | '%' => SEBinOp
        case _                                             => MISMATCH
      }
    }
  }

  def SECons: Rule1[Expr] = rule {
    atomic("cons") ~ SP ~ SExpr ~ SP ~ SExpr ~> (ECons(_, _))
  }

  def SEFst: Rule1[Expr] = rule {
    atomic("fst") ~ SP ~ SExpr ~> (EFst(_))
  }

  def SESnd: Rule1[Expr] = rule {
    atomic("snd") ~ SP ~ SExpr ~> (ESnd(_))
  }

  def SEApp: Rule1[Expr] = rule {
    atomic("app") ~ SP ~ SExpr ~ WL ~ SExprList ~> (EApp(_, _))
  }

  def SELet: Rule1[Expr] = rule {
    atomic("let") ~ SP ~ SBindList ~ WL ~ SExpr ~> (ELet(_, _))
  }

  def SENilP: Rule1[Expr] = rule {
    atomic("nil?") ~ SP ~ SExpr ~> (ENilP(_))
  }

  def SEIntP: Rule1[Expr] = rule {
    atomic("int?") ~ SP ~ SExpr ~> (EIntP(_))
  }

  def SEFloatP: Rule1[Expr] = rule {
    atomic("float?") ~ SP ~ SExpr ~> (EFloatP(_))
  }

  def SEStringP: Rule1[Expr] = rule {
    atomic("string?") ~ SP ~ SExpr ~> (EStringP(_))
  }

  def SEPairP: Rule1[Expr] = rule {
    atomic("pair?") ~ SP ~ SExpr ~> (EPairP(_))
  }

  def SESubstr: Rule1[Expr] = rule {
    atomic("substr") ~ SP ~ SExpr ~ SP
      ~ SExpr ~ SP ~ SExpr ~> (ESubstr(_, _, _))
  }

  def SELen: Rule1[Expr] = rule {
    atomic("len") ~ SP ~ SExpr ~> (ELen(_))
  }

  def SEIf: Rule1[Expr] = rule {
    atomic("if") ~ SP ~ SExpr ~ SP ~ SExpr ~ SP ~ SExpr ~> (EIf(_, _, _))
  }

  def SEBinOp: Rule1[Expr] = rule {
    capture(anyOf("+-*/%=<>")) ~ SP ~
      SExpr ~ SP ~ SExpr ~> ((c: String, left: Expr, right: Expr) =>
        c match {
          case "+" => EAdd(left, right)
          case "-" => ESub(left, right)
          case "*" => EMul(left, right)
          case "/" => EDiv(left, right)
          case "%" => EMod(left, right)
          case "=" => EEq(left, right)
          case "<" => ELt(left, right)
          case _   => EGt(left, right)
        }
      )
  }

  def SArg: Rule1[Arg] = rule {
    (Ident ~> (AVName(_)))
      | ('(' ~ WL ~ "by-name" ~ SP ~ Ident ~ WL ~ ")" ~> (ANName(_)))
  }

  def SArgList: Rule1[List[Arg]] = rule {
    '(' ~ WL ~ zeroOrMore(SArg ~ (&(')') | SP)) ~ ')' ~> ((args: Seq[Arg]) =>
      args.toList
    )
  }

  def SBind: Rule1[Bind] = rule {
    '(' ~ WL ~ (SBindDefIO | SBindDef | SBindVal | SBindLazyVal) ~ WL ~ ')'
  }

  def SBindList: Rule1[List[Bind]] = rule {
    zeroOrMore(SBind ~ WL) ~> ((args: Seq[Bind]) => args.toList)
  }

  def SBindDef: Rule1[Bind] = rule {
    atomic("def") ~ SP ~ Ident ~ SP ~ SArgList ~ SP ~ SExpr ~> (
      (f: String, params: List[Arg], body: Expr) => BDef(f, params, body)
    )
  }

  def SBindVal: Rule1[Bind] = rule {
    atomic("val") ~ SP ~ Ident ~ SP ~ SExpr ~> (BVal(_, _))
  }

  def SBindLazyVal: Rule1[Bind] = rule {
    atomic("lazy-val") ~ SP ~ Ident ~ SP ~ SExpr ~> (BLVal(_, _))
  }

  def SBindDefIO: Rule1[Bind] = rule {
    atomic(
      "defIO"
    ) ~ SP ~ Ident ~ SP ~ SArgList ~ SP ~ SIOActionList ~ SP ~ SExpr ~> (
      (f: String, params: List[Arg], actions: List[IOAction], returns: Expr) =>
        BDefIO(f, params, actions, returns)
    )
  }

  def SIOActionList: Rule1[List[IOAction]] = rule {
    zeroOrMore('(' ~ WL ~ SIOAction ~ WL ~ ')') ~> ((args: Seq[IOAction]) =>
      args.toList
    )
  }

  def SIOAction: Rule1[IOAction] = rule {
    atomic("runIO") ~ SP ~ Ident ~ SP ~ SExpr ~> (IORun(_, _)) |
      atomic("readline") ~ SP ~ Ident ~> (IOReadLine(_)) |
      atomic("print") ~ SP ~ SExpr ~> (IOPrint(_))
  }

  def Ident: Rule1[String] = rule {
    !CharPredicate.Digit ~ capture((CharPredicate.AlphaNum | '_').+)
  }

  def ParIdent: Rule1[String] = rule {
    '(' ~ WL ~ Ident ~ WL ~ ')'
  }

  def SEInteger: Rule1[Int] = rule {
    capture(ch('-').? ~ CharPredicate.Digit.+) ~> ((s: String) => s.toInt)
  }

  def SEFloat: Rule1[Float] = rule {
    capture(
      ch('-').? ~ CharPredicate.Digit.+ ~ '.' ~ CharPredicate.Digit.*
    ) ~> ((s: String) => s.toFloat)
  }

  def SEString: Rule1[String] = rule {
    '"' ~ capture(noneOf("\"").*) ~ '"'
  }

  def WL: Rule0 = rule {
    quiet(anyOf(" \t\r\n").*)
  }

  def SP: Rule0 = rule {
    anyOf(" \t\r\n").+
  }
}
