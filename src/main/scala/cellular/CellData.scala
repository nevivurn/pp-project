package pp202302.assign4.cellular

case class CellState(name: String, index: Int)
case class Grid[A](cells: Vector[Vector[A]])

given vectorIL[State]: IndexedLike[Vector, Int, State] with
  def fill(size: Int)(elem: State): Vector[State] =
    Vector.fill(size)(elem)

  extension (l: Vector[State])
    def size: Int = l.length
    def get(i: Int): State = l(i)

given gridIL[State]: IndexedLike[Grid, (Int, Int), State] with
  def fill(size: (Int, Int))(elem: State): Grid[State] =
    val cells = Vector.fill(size._1)(Vector.fill(size._2)(elem))
    Grid(cells)

  extension (l: Grid[State])
    def size: (Int, Int) = (l.cells.length, l.cells(0).length)
    def get(i: (Int, Int)): State = l.cells(i._1)(i._2)

given vectorAuto[State](using
    cr: CellRule[Vector, Int, State],
    il: IndexedLike[Vector, Int, State]
): CellAutomata[Vector, Int, State] with
  def empty(size: Int): Vector[State] =
    Vector.fill(size)(cr.defaultState)

  extension (ca: Vector[State])
    def setStateAt(pos: Int, state: State): Vector[State] =
      ca.updated(pos, state)
    def neighborsAt(pos: Int): Vector[State] =
      val lo = if pos > 0 then ca.get(pos-1) else cr.defaultState
      val hi = if pos < ca.size-1 then ca.get(pos+1) else cr.defaultState
      Vector(lo, ca.get(pos), hi)
    def step: Vector[State] =
      ca.zipWithIndex.map((v: State, i: Int) =>
          cr.nextState(v, ca.neighborsAt(i)))

given gridAuto[State](using
    cr: CellRule[Grid, (Int, Int), State],
    il: IndexedLike[Grid, (Int, Int), State]
): CellAutomata[Grid, (Int, Int), State] with
  def empty(size: (Int, Int)): Grid[State] =
    il.fill(size)(cr.defaultState)

  extension (ca: Grid[State])
    def setStateAt(pos: (Int, Int), state: State): Grid[State] =
      Grid(ca.cells.updated(pos(0), ca.cells(pos(0)).updated(pos(1), state)))
    def neighborsAt(pos: (Int, Int)): Grid[State] =
      val (x, y) = pos

      val nx = x > 0
      val px = x < ca.size(0)-1
      val ny = y > 0
      val py = y < ca.size(1)-1

      val a = if (nx && ny) ca.get((x-1, y-1)) else cr.defaultState
      val b = if (nx) ca.get(x-1, y) else cr.defaultState
      val c = if (nx && py) ca.get(x-1, y+1) else cr.defaultState

      val d = if (ny) ca.get(x, y-1) else cr.defaultState
      val e = if (py) ca.get(x, y+1) else cr.defaultState

      val f = if (px && ny) ca.get(x+1, y-1) else cr.defaultState
      val g = if (px) ca.get(x+1, y) else cr.defaultState
      val h = if (px && py) ca.get(x+1, y+1) else cr.defaultState

      Grid(Vector(
        Vector(a, b, c),
        Vector(d, e),
        Vector(f, g, h)))

    def step: Grid[State] =
      Grid(ca.cells.zipWithIndex.map((v: Vector[State], x: Int) =>
          v.zipWithIndex.map((s: State, y: Int) =>
              cr.nextState(s, ca.neighborsAt(x, y)))))
