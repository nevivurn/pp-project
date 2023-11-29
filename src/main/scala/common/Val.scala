package pp202302.project.common

enum Val:
  case VInt(n: Int)
  case VFloat(f: Float)
  case VString(s: String)
  case VNil
  case VCons(head: Val, tail: Val)
  case VFunc[Env](funcName: String, params: List[Arg], body: Expr, env: Env)
  case VIOAction[Env](
      actionName: String,
      params: List[Arg],
      actions: List[IOAction],
      returns: Expr,
      env: Env
  )
  case VIOThunk[Env](action: VIOAction[Env], args: List[Val])
