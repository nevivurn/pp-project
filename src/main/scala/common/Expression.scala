package pp202302.project.common

enum Arg:
  case AVName(x: String) // call by value
  case ANName(x: String) // call by name

enum Bind:
  case BDef(f: String, params: List[Arg], body: Expr)
  case BVal(x: String, e: Expr)
  case BLVal(x: String, e: Expr)
  case BDefIO(
      f: String,
      params: List[Arg],
      actions: List[IOAction],
      returns: Expr
  )

enum Expr:
  case EInt(n: Int)
  case EFloat(f: Float)
  case EString(s: String)
  case EName(x: String)
  case ENil
  case ECons(head: Expr, tail: Expr)
  case EFst(e: Expr)
  case ESnd(e: Expr)
  case ENilP(e: Expr)
  case EIntP(e: Expr)
  case EFloatP(e: Expr)
  case EStringP(e: Expr)
  case EPairP(e: Expr)
  case ESubstr(e: Expr, start: Expr, end: Expr)
  case ELen(e: Expr)
  case EIf(cond: Expr, ifTrue: Expr, ifFalse: Expr)
  case ELet(bs: List[Bind], e: Expr)
  case EApp(f: Expr, args: List[Expr])
  case EAdd(left: Expr, right: Expr)
  case ESub(left: Expr, right: Expr)
  case EMul(left: Expr, right: Expr)
  case EDiv(left: Expr, right: Expr)
  case EMod(left: Expr, right: Expr)
  case EEq(left: Expr, right: Expr)
  case ELt(left: Expr, right: Expr)
  case EGt(left: Expr, right: Expr)

enum IOAction:
  case IORun(x: String, e: Expr)
  case IOReadLine(x: String)
  case IOPrint(e: Expr)
