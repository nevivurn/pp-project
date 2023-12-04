package pp202302.assign3.cellular

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
class WireWorld(val map: CellMap) extends CellAutomata(WireWorld.rule):
  def getMap = map

  def createNew(map: CellMap): WireWorld = new WireWorld(map)

  def setStateAt(x: Int, y: Int, state: CellState): WireWorld =
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

object WireWorld:
  def initMap(height: Int, width: Int): CellMap =
    CellMap(
      (height, width),
      Vector.fill(height)(Vector.fill(width)(rule.defaultState))
    )

  def apply(height: Int, width: Int) = new WireWorld(initMap(height, width))

  val rule = new CellRule:
    val cellStates = Vector(
      CellState("empty", 0),
      CellState("conductor", 1),
      CellState("electron_head", 2),
      CellState("electron_tail", 3)
    )
    val defaultState = cellStates(0)
    def nextState(
        currState: CellState,
        neighborsStates: Iterable[CellState]
    ): CellState =
      if (currState == cellStates(0))
        currState
      else if (currState == cellStates(1))
        val count = neighborsStates.count(_ == cellStates(2))
        if (count == 1 || count == 2) cellStates(2) else currState
      else if (currState == cellStates(2))
        cellStates(3)
      else if (currState == cellStates(3))
        cellStates(1)
      else
        currState
