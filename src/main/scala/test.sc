import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession, Row => SRow}
import org.apache.spark.rdd.RDD
import java.io.FileInputStream

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



def getCellValue(cell: Cell): Any = {
  evaluator.evaluateFormulaCell(cell) match {
    case CellType.BOOLEAN => cell.getBooleanCellValue
    case CellType.STRING => cell.getStringCellValue
    case CellType.NUMERIC => cell.getNumericCellValue
    case _ => None
  }
}

val lst: List[List[Any]] = sheet map { row: Row =>
  row.asScala map {cell: Cell => getCellValue(cell)} toList
} toList

System.out.println(lst.toDF)
