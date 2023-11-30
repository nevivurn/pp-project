package pp202302.project.common

enum Val:
  case VInt(n: Int)
  case VFloat(f: Float)
  case VString(s: String)
  case VNil
  case VCons(head: Val, tail: Val)

  // Since `Env` is a type parameter and can be different to `MapEnv`
  // You should match this value like `case VFunc[Env](f, p, b, e) => ...`
  // even it shows `type test for ... cannot be checked` warning.
  // This is also applied to `VIOAction` and `VIOThunk`.
  case VFunc[Env](funcName: String, params: List[Arg], body: Expr, env: Env)
  case VIOAction[Env](
      actionName: String,
      params: List[Arg.AVName],
      actions: List[IOAction],
      returns: Expr,
      env: Env
  )
  case VIOThunk[Env](action: VIOAction[Env], args: List[Val])
