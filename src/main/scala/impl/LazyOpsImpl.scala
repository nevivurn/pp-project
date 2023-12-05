package pp202302.project.impl

import pp202302.project.common._

given LazyOps[Val, LazyVal[Val]] with
  def pend(pending: () => Val): LazyVal[Val] = LVLazy(pending)

  extension (value: Val)
    def toLazy: LazyVal[Val] = LVVal(value)

  extension (value: LazyVal[Val])
    def evaluate: Val =
      value match {
        case LVVal(v) => v
        case LVLazy(pending) => LVLazy(pending).evaluated
      }
