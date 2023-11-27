package pp202302.assign4.cellular

/** Conway's Game of Life is a special case of two-dimensional cellular
  * automaton.
  *
  * For each cell, if the cell is alive, and the number of alive neighbors is 2
  * or 3, then the cell is alive at the next time step. If the cell is dead, and
  * the number of alive neighbors is 3, then the cell is alive at the next time
  * step.
  *
  * Reference: https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
  */
object GameOfLife extends CellRule[Grid, (Int, Int), CellState]:
  val cellStates = Vector(CellState("0", 0), CellState("1", 1))
  val defaultState = cellStates(0)
  def nextState(
      currState: CellState,
      neighborsStates: Grid[CellState]
  ): CellState = ???