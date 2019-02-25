import java.io.FileInputStream
import java.lang.NullPointerException
import org.apache.poi.ss.usermodel.{Cell, CellType, FormulaEvaluator, Row}
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import collection.JavaConverters._

val inp = new FileInputStream("/Users/warrenronsiek/Projects/CRFSC/src/main/scala/C10001.xls")
val wb = new HSSFWorkbook(new POIFSFileSystem(inp))
val sheet: Iterable[Row] = wb.getSheetAt(0).asScala
val evaluator = wb.getCreationHelper.createFormulaEvaluator

object CellTypeSC extends Enumeration {
  type CellTypeSC = Value
  val BOOL, STR, NUM, NONE = Value
}

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

def getCellType(cell:Cell): CellTypeSC.CellTypeSC = {
  try {
    evaluator.evaluate(cell).getCellType match {
      case CellType.BOOLEAN => CellTypeSC.BOOL
      case CellType.STRING => CellTypeSC.STR
      case CellType.NUMERIC => CellTypeSC.NUM
      case _ => CellTypeSC.NONE
    }
  } catch {
    case nullEx: NullPointerException => CellTypeSC.NONE
  }
}


val lst = sheet map { row: Row =>
  row.asScala map { cell: Cell => getCellValue(cell) } toSeq
} toSeq

sheet.size


val r = sheet.slice(1, 6) map {row => row.getLastCellNum}



