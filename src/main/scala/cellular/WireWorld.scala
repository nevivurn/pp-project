package pp202302.assign4.cellular

/** Wireworld is a 2D cellular automaton with 4 states: empty, conductor,
  * electron head, and electron tail.
  *
  * For each cell, if the cell is empty, then it remains empty at the next time
  * step. If the cell is a conductor, and the number of electron heads in its
  * neighbors is 1 or 2, then the cell becomes an electron head at the next time
  * step. If the cell is an electron head, then it becomes an electron tail at
  * the next time step. If the cell is an electron tail, then it becomes a
  * conductor at the next time step.
  *
  * Reference: https://en.wikipedia.org/wiki/Wireworld
  */
object WireWorld extends CellRule[Grid, (Int, Int), CellState]:
  val cellStates = Vector(
    CellState("empty", 0),
    CellState("conductor", 1),
    CellState("electron_head", 2),
    CellState("electron_tail", 3)
  )
  val defaultState = cellStates(0)
  def nextState(
      currState: CellState,
      neighborsStates: Grid[CellState]
  ): CellState =
    if (currState == cellStates(0))
      currState
    else if (currState == cellStates(1))
      val count = neighborsStates.cells.flatten.count(_ == cellStates(2))
      if (count == 1 || count == 2) cellStates(2) else currState
    else if (currState == cellStates(2))
      cellStates(3)
    else if (currState == cellStates(3))
      cellStates(1)
    else
      currState
