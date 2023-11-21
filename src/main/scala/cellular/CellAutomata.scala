package pp202302.assign3.cellular

/** CellAutomata is an abstract class that represents a cellular automaton.
  *
  * Reference: https://en.wikipedia.org/wiki/Cellular_automaton
  *
  * For each cell in the automaton, there is a state. The state can be
  * represented by a CellState object. The state of the cell is determined by
  * the states of its neighbors and the rule of the automaton.
  *
  * @param rule
  *   The rule of the automaton.
  */
trait CellAutomata(val rule: CellRule):
  /** The state map of the automaton. */
  def getMap: CellMap

  /** Create a new automaton with the given map.
    *
    * @param map
    *   The map of the new automaton.
    * @return
    *   The new automaton.
    */
  def createNew(map: CellMap): CellAutomata

  /** Get the state of the cell at (x, y).
    *
    * @param x
    *   The x coordinate of the cell.
    * @param y
    *   The y coordinate of the cell.
    * @return
    *   The state of the cell.
    */
  def getStateAt(x: Int, y: Int): CellState = getMap.grids(x)(y)

  /** Set the state of the cell at (x, y).
    *
    * @param x
    *   The x coordinate of the cell.
    * @param y
    *   The y coordinate of the cell.
    * @param state
    *   The new state of the cell.
    * @return
    *   The new automaton with the state of the cell at (x, y) set to `state`.
    */
  def setStateAt(x: Int, y: Int, state: CellState): CellAutomata

  /** Get the states of the neighbors of the cell at (x, y).
    *
    * When the cell is at the edge of the automaton, the neighbors outside the
    * automaton are considered to be in the default state.
    *
    * @param x
    *   The x coordinate of the cell.
    * @param y
    *   The y coordinate of the cell.
    * @return
    *   The states of the neighbors of the cell at (x, y).
    */
  def neighborsAt(x: Int, y: Int): Iterable[CellState]

  /** Clear the automaton.
    *
    * @return
    *   The new automaton with all cells set to the default state.
    */
  def clear: CellAutomata = createNew(
    CellMap(
      getMap.size,
      Vector.fill(getMap.size._1)(
        Vector.fill(getMap.size._2)(rule.defaultState)
      )
    )
  )

  /** Return a new automaton after one time step. */
  def step(): CellAutomata =
    val map = getMap
    val size = map.size
    val seq =
      for x <- 0 until size._1 yield
        val rowCells =
          for y <- 0 until size._2
          yield rule
            .nextState(
              getStateAt(x, y),
              neighborsAt(x, y)
            )
        rowCells
    val newCells = seq.map(_.toVector).toVector

    createNew(CellMap(size, newCells))
