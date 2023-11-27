package pp202302.assign4.cellular

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
enum Elementary extends CellRule[Vector, Int, CellState]:
  case Rule110
  case RuleByCode(code: Int)

  val cellStates = Vector(CellState("0", 0), CellState("1", 1))
  val defaultState = cellStates(0)
  def nextState(
      currState: CellState,
      neighborsStates: Vector[CellState]
  ): CellState =
    this match {
      case Elementary.Rule110 =>
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
      case Elementary.RuleByCode(code) => ??? // TODO
    }
