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

  def setStateAt(x: Int, y: Int, state: CellState): GameOfLife = ???

  def neighborsAt(x: Int, y: Int): Iterable[CellState] = ???

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
    ): CellState = ???
