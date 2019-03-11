import java.io.FileInputStream
import java.lang.NullPointerException
import scala.collection.JavaConversions._

import org.apache.poi.hssf.usermodel.{HSSFFormulaEvaluator, HSSFSheet, HSSFWorkbook}
import org.apache.poi.ss.usermodel._
import org.apache.poi.hssf.usermodel.{HSSFFormulaEvaluator, HSSFWorkbook}
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import utils.CellWrapper
import utils.CellTypeSC
import collection.JavaConverters._
val inp = new FileInputStream("/Users/warrenronsiek/Projects/CRFSC/src/main/scala/C10001.xls")
val wb: HSSFWorkbook = new HSSFWorkbook(new POIFSFileSystem(inp))
val sheet: Iterable[Row] = wb.getSheetAt(0).asScala

val vallst = sheet map { row: Row =>
  row.asScala map { cell: Cell => new CellWrapper(cell, wb) }
}

class SheetWrapper(wb: HSSFWorkbook) {
  val sheet = wb.getSheetAt(0).asScala
  val valueList = sheet map { row: Row => row.asScala map { cell: Cell => new CellWrapper(cell, wb) } }
  val mergedCells = wb.getSheetAt(0).getMergedRegions
  val ncols = sheet map { row => row.getLastCellNum } max
  val nrows = sheet size

  implicit class CellList(row: List[CellWrapper]) {
    def getRowString: String = row.foldLeft(" ")({(a: String, b: CellWrapper) => b.toString + a })

    def getValueList: List[String] = row map { c => c.toString }

    def alignments(row: List[CellWrapper]): List[HorizontalAlignment] = row map {c => c.alignment}
  }

  override def toString: String = this.valueList.toString

  def isStringRowFeature(row: List[CellWrapper]): Boolean =
    List(CellTypeSC.STR) == {
      row map { cell => cell.valueType } distinct
    }

  def isBoolRowFeature(row: List[CellWrapper]): Boolean =
    List(CellTypeSC.BOOL) == {
      row map { cell => cell.valueType } distinct
    }

  def highWordCountFeature(row: List[CellWrapper]): Boolean =
    row.getRowString.split(" ").groupBy(identity).mapValues(_.length).maxBy(_._2)._2 >= 2

  def longWordFeature(row: List[CellWrapper]): Boolean = row.getValueList.map(_.length).max >= 40.



}
val s = new SheetWrapper(wb)
val s2 = wb.getSheetAt(0)
val cellrange = s2.getMergedRegions()(1)