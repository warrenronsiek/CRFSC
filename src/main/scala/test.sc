import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession, Row => SRow}
import org.apache.spark.rdd.RDD
import java.io.FileInputStream
import java.lang.NullPointerException
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.poi.ss.usermodel.{Cell, CellType, FormulaEvaluator, Row}
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem

import collection.JavaConverters._

val inp = new FileInputStream("/Users/warrenronsiek/Projects/CRFSC/src/main/scala/C10001.xls")
val wb = new HSSFWorkbook(new POIFSFileSystem(inp))
val sheet: Iterable[Row] = wb.getSheetAt(0).asScala
val evaluator = wb.getCreationHelper.createFormulaEvaluator

val spark = SparkSession
  .builder
  .appName("SparkPOC")
  .config("spark.master", "local")
  .getOrCreate()
val sqlContext = spark.sqlContext

import spark.implicits._
import sqlContext.implicits._

//def getCellValue(cell: Cell): Option[Any] = {
//  val evaledCell = evaluator.evaluate(cell)
//  try {
//    evaledCell.getCellType match {
//      case CellType.BOOLEAN => Some(evaledCell.getBooleanValue)
//      case CellType.STRING => Some(evaledCell.getStringValue)
//      case CellType.NUMERIC => Some(evaledCell.getNumberValue)
//      case _ => None
//    }
//  } catch {
//    case nullEx: NullPointerException => None
//  }
//}

def getCellValue(cell: Cell): Option[Any] = {
  val evaledCell = evaluator.evaluate(cell)
  try {
    evaledCell.getCellType match {
      case CellType.BOOLEAN => Some(evaledCell.getBooleanValue)
      case CellType.STRING => Some(evaledCell.getStringValue)
      case CellType.NUMERIC => Some(evaledCell.getNumberValue)
      case _ => None
    }
  } catch {
    case nullEx: NullPointerException => None
  }
}


val lst = sheet map { row: Row =>
  SRow.fromSeq(row.asScala map { cell: Cell => getCellValue(cell) } toSeq)
} toSeq

RDD(lst)