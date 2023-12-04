package pp202302.project.common

import Arg._
import Bind._
import Expr._
import IOAction._
import Val._

trait Show[-T]:
  extension (t: T) def show: String

// NOTE: implementation of a single function trait can be reduced to
//       single abstract method -> pattern matching anonymous function
//      (See https://bargsten.org/scala/anonymous-class-and-sam/)
given showIter[A](using sh: Show[A]): Show[Iterable[A]] =
  _.map(sh.show).mkString(" ")

given sx: Show[Arg] =
  case AVName(x) => x
  case ANName(x) => s"(by-name $x)"

given Show[Bind] =
  case BVal(x, e)  => s"(val $x ${e.show})"
  case BLVal(x, e) => s"(lazy-val $x ${e.show})"
  case BDef(f, params, body) =>
    s"(def $f (${params.show}) ${body.show})"
  case BDefIO(f, params, actions, returns) => {
    val actionsStr = actions.map(_.show).mkString(" ")
    s"(defIO $f (${params.show}) $actionsStr ${returns.show})"
  }

given Show[Expr] =
  case EInt(n)           => n.toString
  case EFloat(f)         => f.toString
  case EString(s)        => s"\"$s\""
  case EName(x)          => x
  case ENil              => "nil"
  case ECons(head, tail) => s"(cons ${head.show} ${tail.show})"
  case EFst(e)           => s"(fst ${e.show})"
  case ESnd(e)           => s"(snd ${e.show})"
  case ENilP(e: Expr)    => s"(nil? ${e.show})"
  case EIntP(e: Expr)    => s"(int? ${e.show})"
  case EFloatP(e: Expr)  => s"(float? ${e.show})"
  case EStringP(e: Expr) => s"(string? ${e.show})"
  case EPairP(e: Expr)   => s"(pair? ${e.show})"
  case ESubstr(e: Expr, start: Expr, end: Expr) =>
    s"(substr ${e.show} ${start.show} ${end.show})"
  case ELen(e: Expr) => s"(len ${e.show})"
  case EApp(f, args) => s"(app ${f.show} ${args.show})"
  case ELet(bindings: List[Bind], e: Expr) =>
    s"(let (${bindings.map(b => b.show).mkString(" ")})\n${e.show})"
  case EIf(cond, ifTrue, ifFalse) =>
    s"(if ${cond.show}\n${ifTrue.show}\n${ifFalse.show})"
  case EAdd(left: Expr, right: Expr) =>
    s"(+ ${left.show} ${right.show})"
  case ESub(left: Expr, right: Expr) =>
    s"(- ${left.show} ${right.show})"
  case EMul(left: Expr, right: Expr) =>
    s"(* ${left.show} ${right.show})"
  case EDiv(left: Expr, right: Expr) =>
    s"(/ ${left.show} ${right.show})"
  case EMod(left: Expr, right: Expr) =>
    s"(% ${left.show} ${right.show})"
  case EEq(left: Expr, right: Expr) =>
    s"(= ${left.show} ${right.show})"
  case ELt(left: Expr, right: Expr) =>
    s"(< ${left.show} ${right.show})"
  case EGt(left: Expr, right: Expr) =>
    s"(> ${left.show} ${right.show})"

given Show[IOAction] =
  case IORun(x, e)   => s"(runIO $x ${e.show})"
  case IOReadLine(x) => s"(readline $x)"
  case IOPrint(e)    => s"(print ${e.show})"

given Show[Val] =
  case VInt(n: Int)               => n.toString
  case VFloat(f: Float)           => f.toString
  case VString(s: String)         => s
  case VNil                       => "nil"
  case VCons(head, tail)          => s"(${head.show} . ${tail.show})"
  case VFunc(fn, params, body, _) => s"(def $fn (${params.show}) ${body.show})"
  case VIOAction(actionName, params, actions, returns, env) => {
    val actionsStr = actions.map(_.show).mkString(" ")
    s"(defIO $actionName (${params.show}) $actionsStr ${returns.show})"
  }
  case VIOThunk(action, args) =>
    s"<thunk ${action.actionName} ${args.show}>"

given showLazyVal[V](using sv: Show[V]): Show[LazyVal[V]] =
  case LVVal(v)  => v.show
  case LVLazy(_) => "<lazy>"
