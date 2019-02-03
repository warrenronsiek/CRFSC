import java.io.FileInputStream

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.{Cell, Row}

import collection.JavaConverters._

val inp = new FileInputStream("/Users/warrenronsiek/Projects/CRFSC/src/main/scala/C10001.xls")
val wb = new HSSFWorkbook(new POIFSFileSystem(inp))
val sheet: Iterable[Row] = wb.getSheetAt(0).asScala

case class SheetCell(rowIndex: Int, colIndex: Int, value: Any)

val lst = sheet flatMap { row: Row =>
  row.asScala map { cell: Cell => SheetCell(row.getRowNum, cell.getColumnIndex, cell) }
}

System.out.println(lst)