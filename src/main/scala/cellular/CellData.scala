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
    def setStateAt(pos: Int, state: State): Vector[State] = ???
    def neighborsAt(pos: Int): Vector[State] = ???
    def step: Vector[State] = ???

given gridAuto[State](using
    cr: CellRule[Grid, (Int, Int), State],
    il: IndexedLike[Grid, (Int, Int), State]
): CellAutomata[Grid, (Int, Int), State] with
  def empty(size: (Int, Int)): Grid[State] = ???

  // TODO: UNCOMMENT BELOW AND FILL IT!
  // extension (ca: Grid[State])
  // ??? 