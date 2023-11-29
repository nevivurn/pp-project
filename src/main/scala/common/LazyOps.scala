package pp202302.project.common

trait LazyOps[Imm, Lazy]:
  /** Make new lazy value which has a pending operation.
    *
    * @param pending
    *   Lazy value generator
    * @return
    *   lazy thunk
    */
  def pend(pending: () => Imm): Lazy

  extension (value: Imm)
    /** Trivially convert the evaluated value to a lazy value. The value is not
      * pended.
      */
    def toLazy: Lazy

  extension (value: Lazy)
    /** Evaluate the lazy value and generate an immediate value.
      */
    def evaluate: Imm
