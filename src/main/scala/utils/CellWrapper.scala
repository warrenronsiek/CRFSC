package utils

import org.apache.poi.hssf.usermodel.{HSSFFormulaEvaluator, HSSFWorkbook}
import org.apache.poi.ss.usermodel._

object CellTypeSC extends Enumeration {
  type CellTypeSC = Value
  val BOOL, STR, NUM, EMPTY = Value
}

class CellWrapper(cell: Cell, wb: HSSFWorkbook) {
  lazy private val evaluator: HSSFFormulaEvaluator = wb.getCreationHelper.createFormulaEvaluator
  lazy val evaledCell: CellValue = evaluator.evaluate(cell)

  def value: Option[Any] = {
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

  def valueType: CellTypeSC.CellTypeSC = {
    try {
      evaledCell.getCellType match {
        case CellType.BOOLEAN => CellTypeSC.BOOL
        case CellType.STRING => CellTypeSC.STR
        case CellType.NUMERIC => CellTypeSC.NUM
        case _ => CellTypeSC.EMPTY
      }
    } catch {
      case nullEx: NullPointerException => CellTypeSC.EMPTY
    }
  }

  override def toString: String = this.value.toString
}
