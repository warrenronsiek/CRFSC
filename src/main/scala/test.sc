import java.io.FileInputStream
import java.lang.NullPointerException

import breeze.linalg.DenseMatrix

import scala.collection.JavaConversions._
import org.apache.poi.hssf.usermodel.{HSSFFormulaEvaluator, HSSFSheet, HSSFWorkbook}
import org.apache.poi.ss.usermodel._
import org.apache.poi.hssf.usermodel.{HSSFFormulaEvaluator, HSSFWorkbook}
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import utils.{CellTypeSC, CellWrapper, SheetWrapper}

import collection.JavaConverters._
val inp = new FileInputStream("/Users/warrenronsiek/Projects/CRFSC/src/main/scala/C10001.xls")
val wb: HSSFWorkbook = new HSSFWorkbook(new POIFSFileSystem(inp))
val sheet: Iterable[Row] = wb.getSheetAt(0).asScala

val vallst = sheet map { row: Row =>
  row.asScala map { cell: Cell => new CellWrapper(cell, wb) }
}

val s = new SheetWrapper(wb)
s.buildFeatureMatrix()
val s2 = wb.getSheetAt(0)
val cellrange = s2.getMergedRegions()(1)


DenseMatrix(for (i <- List(1, 2); j <- List(3, 4, 5)) yield i + j).reshape(3, 2)