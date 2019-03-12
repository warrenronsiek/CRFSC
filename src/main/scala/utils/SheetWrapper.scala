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

  override def toString: String = this.valueList.toString

  def buildFeatureMatrix(): DenseMatrix[Int] = {
    DenseMatrix({
      for(func <- SheetWrapper.featureFunctions; row <- valueList) yield {
        if (func(row)) 1 else 0
      }
    }).reshape(this.nrows, SheetWrapper.featureFunctions.length)
  }

}

object SheetWrapper {

  implicit class CellList(row: Iterable[CellWrapper]) {
    def getRowString: String = row.foldLeft(" ")({(a: String, b: CellWrapper) => b.toString + a })

    def getValueList: List[String] = row.toList.map { c => c.toString }

    def alignments(row: List[CellWrapper]): List[HorizontalAlignment] = row map {c => c.alignment}
  }

  val isStringRow: Iterable[CellWrapper] => Boolean = (row: Iterable[CellWrapper]) =>
    List(CellTypeSC.STR) == {row.map(_.valueType).toList.distinct}
  val isBoolRow: Iterable[CellWrapper] => Boolean = (row: Iterable[CellWrapper]) =>
    List(CellTypeSC.BOOL) == {row.map(_.valueType).toList.distinct}
  val highWordCount: Iterable[CellWrapper] => Boolean = (row: Iterable[CellWrapper]) =>
  {row.getRowString.split(" ").groupBy(identity).mapValues(_.length).maxBy(_._2)._2 >= 2}
  val longWords: Iterable[CellWrapper] => Boolean = (row: Iterable[CellWrapper]) => row.getValueList.map(_.length).max >= 40

  val featureFunctions: List[Iterable[CellWrapper] => Boolean] = List(
    isStringRow,
    isBoolRow,
    highWordCount,
    longWords
  )

}