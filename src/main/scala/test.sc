import java.io.FileInputStream
import java.lang.NullPointerException
import scala.collection.JavaConversions._

import org.apache.poi.hssf.usermodel.{HSSFFormulaEvaluator, HSSFSheet, HSSFWorkbook}
import org.apache.poi.ss.usermodel._
import org.apache.poi.hssf.usermodel.{HSSFFormulaEvaluator, HSSFWorkbook}
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import utils.CellWrapper

import collection.JavaConverters._
val inp = new FileInputStream("/Users/warrenronsiek/Projects/CRFSC/src/main/scala/C10001.xls")
val wb: HSSFWorkbook = new HSSFWorkbook(new POIFSFileSystem(inp))
val sheet = wb.getSheetAt(0).asScala

val vallst = sheet map { row: Row =>
  row.asScala map { cell: Cell => new CellWrapper(cell, wb) } toSeq
} toSeq
//
//
//val r = sheet.slice(1, 6) map {row => row.getLastCellNum}



