package pp202302.assign3.cellular

/** Elementary Cellular Automaton is a type of cellular automaton that consists
  * of a one-dimensional grid of cells, each of which can have two possible
  * states: "0" and "1". The cells evolve at each time step according to a rule
  * that determines the new state of each cell based on the current state of the
  * cell and the states of its two immediate neighbors.
  *
  * The rule is specified by an integer between 0 and 255, which is converted
  * into a binary number of length 8. The state of the cell at the next time
  * step is determined by the value of the corresponding bit in the binary
  * number.
  *
  * For example, the rule 110 is represented by the binary number 01101110.
  * Assume that the current state and its two neighbors are (1, 0, 1). The
  * corresponding bit in the binary number is 101(2) = 5(10), which is
  * "01101110"[5] == '1'. Therefore, the state of the cell at the next time step
  * is "1".
  *
  * Reference: https://en.wikipedia.org/wiki/Elementary_cellular_automaton
  */

class Elementary(val map: CellMap, rule: CellRule) extends CellAutomata(rule):

  def getMap = map

  def createNew(map: CellMap): Elementary =
    new Elementary(map, rule)

  def setStateAt(x: Int, y: Int, state: CellState): Elementary =
    val newGrids = map.grids.updated(x, map.grids(x).updated(y, state))
    createNew(CellMap(map.size, newGrids))

  def neighborsAt(x: Int, y: Int): Iterable[CellState] =
    if map.size._2 == 1 then
      rule.defaultState :: map.grids(0)(0) :: rule.defaultState :: Nil
    else if y == 0 then
      rule.defaultState :: map.grids(0)(0) :: map.grids(0)(1) :: Nil
    else if y == map.size._2 - 1 then
      map.grids(0)(y - 1) :: map.grids(0)(y) :: rule.defaultState :: Nil
    else map.grids(0)(y - 1) :: map.grids(0)(y) :: map.grids(0)(y + 1) :: Nil

/** The companion object of Elementary.
  */
object Elementary:
  trait BaseRule extends CellRule:
    val cellStates = Vector(CellState("0", 0), CellState("1", 1))
    val defaultState = cellStates(0)

  /** Initialize the map of the automaton.
    *
    * @param len
    *   The length of the map.
    * @return
    *   The empty map.
    */
  def initMap(len: Int): CellMap =
    CellMap((1, len), Vector(Vector.fill(len)(CellState("0", 0))))

  /** Create a new automaton with the given rule.
    *
    * @param len
    *   The length of the map.
    * @param ruleCode
    *   The rule code.
    * @return
    *   The new empty automaton.
    */
  def apply(len: Int, ruleCode: Int) =
    new Elementary(initMap(len), createRule(ruleCode))

  /** Example: Rule 110 */
  val rule110: CellRule = new BaseRule {
    def nextState(
        currState: CellState,
        neighborsStates: Iterable[CellState]
    ): CellState =
      val neighbors = neighborsStates.map(_.name.toInt).toList
      neighbors match {
        case 0 :: 0 :: 0 :: Nil => cellStates(0)
        case 0 :: 0 :: 1 :: Nil => cellStates(1)
        case 0 :: 1 :: 0 :: Nil => cellStates(1)
        case 0 :: 1 :: 1 :: Nil => cellStates(1)
        case 1 :: 0 :: 0 :: Nil => cellStates(0)
        case 1 :: 0 :: 1 :: Nil => cellStates(1)
        case 1 :: 1 :: 0 :: Nil => cellStates(1)
        case 1 :: 1 :: 1 :: Nil => cellStates(0)
        case _                  => cellStates(0)
      }
  }

  /** Create a rule from the rule code.
    *
    * @param ruleCode
    *   The rule code. (0 <= ruleCode <= 255)
    */
  def createRule(ruleCode: Int): CellRule = new BaseRule {
    def nextState(
        currState: CellState,
        neighborsStates: Iterable[CellState]
    ): CellState =
      def isOn(pos: Int): Int =
        if ((ruleCode & (1<<pos)) == (1<<pos)) { 1 } else { 0 }

      val neighbors = neighborsStates.map(_.name.toInt).toList
      neighbors match {
        case 0 :: 0 :: 0 :: Nil => cellStates(isOn(0))
        case 0 :: 0 :: 1 :: Nil => cellStates(isOn(1))
        case 0 :: 1 :: 0 :: Nil => cellStates(isOn(2))
        case 0 :: 1 :: 1 :: Nil => cellStates(isOn(3))
        case 1 :: 0 :: 0 :: Nil => cellStates(isOn(4))
        case 1 :: 0 :: 1 :: Nil => cellStates(isOn(5))
        case 1 :: 1 :: 0 :: Nil => cellStates(isOn(6))
        case 1 :: 1 :: 1 :: Nil => cellStates(isOn(7))
        case _                  => cellStates(0)
      }
  }
