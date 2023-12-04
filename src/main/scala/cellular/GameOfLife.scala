package pp202302.assign3.cellular

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
class GameOfLife(val map: CellMap) extends CellAutomata(GameOfLife.rule):
  def getMap = map

  def createNew(map: CellMap): GameOfLife = new GameOfLife(map)

  def setStateAt(x: Int, y: Int, state: CellState): GameOfLife =
    val newGrids = map.grids.updated(x, map.grids(x).updated(y, state))
    createNew(CellMap(map.size, newGrids))

  def neighborsAt(x: Int, y: Int): Iterable[CellState] =
    val nx = x > 0
    val px = x < map.size(0)-1
    val ny = y > 0
    val py = y < map.size(1)-1

    val a = if (nx && ny) map.grids(x-1)(y-1) else rule.defaultState
    val b = if (nx) map.grids(x-1)(y) else rule.defaultState
    val c = if (nx && py) map.grids(x-1)(y+1) else rule.defaultState

    val d = if (ny) map.grids(x)(y-1) else rule.defaultState
    val e = if (py) map.grids(x)(y+1) else rule.defaultState

    val f = if (px && ny) map.grids(x+1)(y-1) else rule.defaultState
    val g = if (px) map.grids(x+1)(y) else rule.defaultState
    val h = if (px && py) map.grids(x+1)(y+1) else rule.defaultState

    a :: b :: c :: d :: e :: f :: g :: h :: Nil

object GameOfLife:
  def initMap(height: Int, width: Int): CellMap =
    CellMap(
      (height, width),
      Vector.fill(height)(Vector.fill(width)(rule.defaultState))
    )

  def apply(height: Int, width: Int) = new GameOfLife(initMap(height, width))

  val rule = new CellRule:
    val cellStates = Vector(CellState("0", 0), CellState("1", 1))
    val defaultState = cellStates(0)

    def nextState(
        currState: CellState,
        neighborsStates: Iterable[CellState]
    ): CellState =
      val count = neighborsStates.count(_ == cellStates(1))
      if (currState == cellStates(1) && count < 2)
        cellStates(0)
      else if (currState == cellStates(1) && count > 3)
        cellStates(0)
      else if (currState == cellStates(0) && count == 3)
        cellStates(1)
      else
        currState
