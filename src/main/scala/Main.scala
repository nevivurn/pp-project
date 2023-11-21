package pp202302.assign3

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import pp202302.assign3.cellular._

object TutorialApp {
  var automata: CellAutomata =
    new Elementary(Elementary.initMap(10), Elementary.rule110)

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
    automata = automata.step()
    reload()
  }

  @JSExportTopLevel("clear")
  def clear(): Unit = {
    automata = automata.clear
    reload()
  }

  @JSExportTopLevel("changeWidth")
  def changeWidth(): Unit = {
    val widthValue = width.value.toInt
    val heightValue = height.value.toInt
    val elemModeValue = elemMode.value.toInt
    automata = automata match {
      case _: Elementary =>
        Elementary(widthValue, elemModeValue)
      case _: GameOfLife =>
        GameOfLife(heightValue, widthValue)
      case _: WireWorld =>
        WireWorld(heightValue, widthValue)
    }
    reload()
  }

  @JSExportTopLevel("changeHeight")
  def changeHeight(): Unit = {
    val widthValue = width.value.toInt
    val heightValue = height.value.toInt
    val elemModeValue = elemMode.value.toInt
    automata = automata match {
      case _: Elementary =>
        Elementary(widthValue, elemModeValue)
      case _: GameOfLife =>
        GameOfLife(heightValue, widthValue)
      case _: WireWorld =>
        WireWorld(heightValue, widthValue)
    }
    reload()
  }

  @JSExportTopLevel("changeElementary")
  def changeElementary(): Unit = {
    val widthValue = width.value.toInt
    val elemModeValue = elemMode.value.toInt
    automata = automata match {
      case _: Elementary =>
        Elementary(widthValue, elemModeValue)
      case _: GameOfLife =>
        GameOfLife(10, 10)
      case _: WireWorld =>
        WireWorld(10, 10)
    }
    reload()
  }

  @JSExportTopLevel("changeMode")
  def changeMode(): Unit = {
    val modeValue = mode.value
    val widthValue = width.value.toInt
    val heightValue = height.value.toInt
    val elemModeValue = elemMode.value.toInt

    modeValue match {
      case "elementary" =>
        automata = Elementary(widthValue, elemModeValue)
      case "gameoflife" =>
        automata = GameOfLife(heightValue, widthValue)
      case "wireworld" =>
        automata = WireWorld(heightValue, widthValue)
    }
    reload()
  }

  def genNode(x: Int, y: Int, stateId: Int) = {
    val cell = document.createElement("div").asInstanceOf[dom.html.Div]
    cell.classList.add("cell")
    cell.id = s"cell-$x-$y"
    cell.className = s"cell cell-$stateId"
  }

  def reload(): Unit = {
    val map = automata.getMap
    plane.innerHTML = ""

    automata match {
      case _: Elementary =>
        hline.style.display = "none"
        elemline.style.display = "block"
      case _: GameOfLife =>
        hline.style.display = "block"
        elemline.style.display = "none"
      case _: WireWorld =>
        hline.style.display = "block"
        elemline.style.display = "none"
    }

    val rows = map.grids.zipWithIndex.map { case (row, x) =>
      val rowNode = document.createElement("div").asInstanceOf[dom.html.Div]
      rowNode.classList.add("row")
      rowNode.id = s"row-$x"
      val cells = row.zipWithIndex.map { case (cell, y) =>
        val cellNode = document.createElement("div").asInstanceOf[dom.html.Div]
        cellNode.classList.add("cell")
        cellNode.id = s"cell-$x-$y"
        cellNode.className = s"cell cell-${cell.index}"
        cellNode.onclick = (e: dom.MouseEvent) => {
          val newState = automata.getStateAt(x, y).index match {
            case 0 => 1
            case 1 => if (mode.value == "wireworld") 2 else 0
            case 2 => 3
            case 3 => 0
          }
          automata =
            automata.setStateAt(x, y, automata.rule.cellStates(newState))
          cellNode.className = s"cell cell-$newState"
        }
        cellNode
      }
      cells.foreach(rowNode.appendChild)
      rowNode
    }
    rows.foreach(plane.appendChild)
    plane.style.height = s"${map.size._1 * 10}px"
    plane.style.width = s"${map.size._2 * 10}px"
  }

  def main(args: Array[String]): Unit = {
    reload()
  }
}
