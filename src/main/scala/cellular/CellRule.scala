package pp202302.assign3.cellular

case class CellState(name: String, index: Int)
case class CellMap(size: (Int, Int), grids: Vector[Vector[CellState]])

trait CellRule:
  val cellStates: Vector[CellState]
  val defaultState: CellState
  def nextState(
      currState: CellState,
      neighborsStates: Iterable[CellState]
  ): CellState
