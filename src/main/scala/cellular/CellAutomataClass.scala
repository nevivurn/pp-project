package pp202302.assign4.cellular

// Type class
trait IndexedLike[IL[_], Idx, A]:
  def fill(size: Idx)(elem: A): IL[A]

  extension (l: IL[A])
    def size: Idx
    def get(i: Idx): A

// Normal Interface
trait CellRule[IL[_], Idx, State](using il: IndexedLike[IL, Idx, State]):
  val cellStates: Vector[State]
  val defaultState: State
  def nextState(
      currState: State,
      neighborsStates: IL[State]
  ): State

// Type class
trait CellAutomata[IL[_], Idx, State](using
    cr: CellRule[IL, Idx, State],
    il: IndexedLike[IL, Idx, State]
):
  def init(size: Idx): IL[State] = il.fill(size)(cr.defaultState)

  extension (ca: IL[State])
    def getStateAt(pos: Idx): State = ca.get(pos)
    def setStateAt(pos: Idx, state: State): IL[State]
    def neighborsAt(pos: Idx): IL[State]

    def clear: IL[State] = il.fill(ca.size)(cr.defaultState)
    def step: IL[State]
