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
    DenseMatrix(
      for (func <- SheetWrapper.featureFunctions; row: Iterable[CellWrapper] <- valueList) yield {
        if (func(row)) 1 else 0
      }
    ).reshape(this.nrows, SheetWrapper.featureFunctions.length)
  }
}

object SheetWrapper {

  implicit class CellList(row: Iterable[CellWrapper]) {
    def getRowString: String = row.foldLeft(" ")({ (a: String, b: CellWrapper) => b.toString + a })

    def getValueList: List[String] = row.toList.map { c => c.toString }

    def alignments(row: List[CellWrapper]): List[HorizontalAlignment] = row map { c => c.alignment }
  }

  private def sliding3Iter(cellOperator: (CellWrapper, CellWrapper, CellWrapper) => Boolean, sheet: Iterable[Iterable[CellWrapper]]): List[Boolean] = {
    val rowOperator: (Iterable[CellWrapper], Iterable[CellWrapper], Iterable[CellWrapper]) => Iterable[Boolean] = (previousRow, currentRow, nextRow) =>
      previousRow zip currentRow zip nextRow map { case ((i,c),s) => cellOperator(i,c,s)}
    sheet.sliding(3).collect { case ls if ls.size == 3 => rowOperator(ls(0), ls(1), ls(2)) }
  }


  val isStringRow: Iterable[CellWrapper] => Boolean = (row: Iterable[CellWrapper]) =>
    List(CellTypeSC.STR) == {
      row.map(_.valueType).toList.distinct
    }
  val isBoolRow: Iterable[CellWrapper] => Boolean = (row: Iterable[CellWrapper]) =>
    List(CellTypeSC.BOOL) == {
      row.map(_.valueType).toList.distinct
    }
  val highWordCount: Iterable[CellWrapper] => Boolean = (row: Iterable[CellWrapper]) => {
    row.getRowString.split(" ").groupBy(identity).mapValues(_.length).maxBy(_._2)._2 >= 2
  }
  val longWords: Iterable[CellWrapper] => Boolean = (row: Iterable[CellWrapper]) => row.getValueList.map(_.length).max >= 40

  def neighborRowsAreStrings(rowIterator: Iterable[Iterable[CellWrapper]]): Boolean = {
    val neighborCellsAreStrings: (CellWrapper, CellWrapper, CellWrapper) => Boolean =
      (preceding, current, subsequent) => (preceding.valueType == CellTypeSC.STR) && (subsequent.valueType == CellTypeSC.STR)
    sliding3Iter[Iterable[CellWrapper], Boolean](neighborCellsAreStrings, rowIterator).reduce(_ && _)
  }

  val featureFunctions: List[Iterable[CellWrapper] => Boolean] = List(
    isStringRow,
    isBoolRow,
    highWordCount,
    longWords
  )
}