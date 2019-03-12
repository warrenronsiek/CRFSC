package utils

import java.util

import breeze.linalg.DenseMatrix
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.{Cell, HorizontalAlignment, Row}
import org.apache.poi.ss.util.CellRangeAddress

import collection.JavaConverters._


class SheetWrapper(wb: HSSFWorkbook) {
  val sheet: Iterable[Row] = wb.getSheetAt(0).asScala
  val valueList: Iterable[Iterable[CellWrapper]] =
    sheet map { row: Row => row.asScala map { cell: Cell => new CellWrapper(cell, wb) } }
  val mergedCells: util.List[CellRangeAddress] = wb.getSheetAt(0).getMergedRegions
  val ncols: Int = sheet map { row => row.getLastCellNum } max
  val nrows: Int = sheet.size

  implicit class CellList(row: List[CellWrapper]) {
    def getRowString: String = row.foldLeft(" ")({(a: String, b: CellWrapper) => b.toString + a })

    def getValueList: List[String] = row map { c => c.toString }

    def alignments(row: List[CellWrapper]): List[HorizontalAlignment] = row map {c => c.alignment}
  }

  implicit def boolToInt(b: Boolean): Int = if (b) 1 else 0

  override def toString: String = this.valueList.toString

  def isStringRowFeature(row: Iterable[CellWrapper]): Boolean =
    List(CellTypeSC.STR) == {row.map(_.valueType).toList.distinct}

  def isBoolRowFeature(row: Iterable[CellWrapper]): Boolean =
    List(CellTypeSC.BOOL) == {row.map(_.valueType).toList.distinct}

  def highWordCountFeature(row: List[CellWrapper]): Boolean =
    row.getRowString.split(" ").groupBy(identity).mapValues(_.length).maxBy(_._2)._2 >= 2

  def longWordFeature(row: List[CellWrapper]): Boolean = row.getValueList.map(_.length).max >= 40

  def buildFeatureMatrix(): DenseMatrix[Int] = {
    DenseMatrix(valueList.map(this.isBoolRowFeature).map(boolToInt).toList).t
  }

}

object SheetWrapper {
  val featureFunctions: List[(Iterable[CellWrapper]) => Boolean] = {

  }
}