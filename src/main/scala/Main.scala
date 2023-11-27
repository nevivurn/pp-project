package pp202302.assign4

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import pp202302.assign4.cellular.{_, given}

object TutorialApp {
  var grid = Grid(Vector.fill(10)(Vector.fill(10)(CellState("0", 0))))
  var state: (Int, Int, Int) = (10, 10, 110)

  val mode = document.getElementById("modeSelect").asInstanceOf[dom.html.Select]
  val width = document.getElementById("width").asInstanceOf[dom.html.Input]
  val height = document.getElementById("height").asInstanceOf[dom.html.Input]
  val elemMode =
    document.getElementById("elemMode").asInstanceOf[dom.html.Input]
  val plane = document.getElementById("plane").asInstanceOf[dom.html.Div]
  val hline = document.getElementById("hline").asInstanceOf[dom.html.Div]
  val elemline = document.getElementById("elemline").asInstanceOf[dom.html.Div]

  @JSExportTopLevel("step")
  def step(): Unit = {
    val ruleCode = state._3

    if mode.value == "elementary" then
      implicit val rule = Elementary.RuleByCode(ruleCode)
      val vec = grid.cells(0)
      grid = Grid(Vector(vec.step))
    else
      implicit val rule =
        if mode.value == "gameoflife" then GameOfLife else WireWorld
      grid = grid.step

    reload()
  }

  @JSExportTopLevel("clearAll")
  def clearAll(): Unit = {
    val ruleCode = state._3

    if mode.value == "elementary" then
      implicit val rule = Elementary.RuleByCode(ruleCode)
      val vec = grid.cells(0)
      grid = Grid(Vector(vec.clear))
    else
      implicit val rule =
        if mode.value == "gameoflife" then GameOfLife else WireWorld
      grid = grid.clear
    reload()
  }

  def resetState(): Unit =
    val widthValue = width.value.toInt
    val heightValue = height.value.toInt
    val ruleCode = elemMode.value.toInt

    state = (widthValue, heightValue, ruleCode)

  def resetGrid(): Unit = {
    resetState()
    val (widthValue, heightValue, ruleCode) = state

    if mode.value == "elementary" then
      implicit val rule = Elementary.RuleByCode(ruleCode)
      val vec = grid.cells(0)
      grid = Grid(Vector(Vector.fill(widthValue)(rule.defaultState)))
    else
      implicit val rule =
        if mode.value == "gameoflife" then GameOfLife else WireWorld
      grid = gridIL.fill((heightValue, widthValue))(rule.defaultState)

    reload()
  }

  @JSExportTopLevel("changeWidth")
  def changeWidth(): Unit = {
    resetGrid()
    reload()
  }

  @JSExportTopLevel("changeHeight")
  def changeHeight(): Unit = {
    resetGrid()
    reload()
  }

  @JSExportTopLevel("changeElementary")
  def changeElementary(): Unit = {
    resetGrid()
    reload()
  }

  @JSExportTopLevel("changeMode")
  def changeMode(): Unit = {
    resetGrid()
    reload()
  }

  def genNode(x: Int, y: Int, stateId: Int) = {
    val cell = document.createElement("div").asInstanceOf[dom.html.Div]
    cell.classList.add("cell")
    cell.id = s"cell-$x-$y"
    cell.className = s"cell cell-$stateId"
  }

  def reload(): Unit = {
    val map = grid.cells
    val size = grid.size

    plane.innerHTML = ""

    val rows = map.zipWithIndex.map { case (row, x) =>
      val rowNode = document.createElement("div").asInstanceOf[dom.html.Div]
      rowNode.classList.add("row")
      rowNode.id = s"row-$x"
      val cells = row.zipWithIndex.map { case (cell, y) =>
        val cellNode = document.createElement("div").asInstanceOf[dom.html.Div]
        cellNode.classList.add("cell")
        cellNode.id = s"cell-$x-$y"
        cellNode.className = s"cell cell-${cell.index}"
        cellNode.onclick = (e: dom.MouseEvent) => {
          if mode.value == "elementary" then
            implicit val rule = Elementary.RuleByCode(state._3)
            val vec = grid.cells(0)
            val newState = vec.getStateAt(y).index match {
              case 0 => 1
              case 1 => 0
            }
            grid = Grid(Vector(vec.setStateAt(y, rule.cellStates(newState))))
            cellNode.className = s"cell cell-$newState"
          else
            implicit val rule =
              if mode.value == "gameoflife" then GameOfLife else WireWorld
            val newState = grid.getStateAt((x, y)).index match {
              case 0 => 1
              case 1 => if (mode.value == "wireworld") 2 else 0
              case 2 => 3
              case 3 => 0
            }
            grid = grid.setStateAt((x, y), rule.cellStates(newState))
            cellNode.className = s"cell cell-$newState"

        }
        cellNode
      }
      cells.foreach(rowNode.appendChild)
      rowNode
    }
    rows.foreach(plane.appendChild)
    plane.style.height = s"${size._1 * 10}px"
    plane.style.width = s"${size._2 * 10}px"
  }

  def main(args: Array[String]): Unit = {
    resetGrid()
    reload()
  }
}
