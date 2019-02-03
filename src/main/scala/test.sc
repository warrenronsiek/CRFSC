import java.io.FileInputStream

import breeze.linalg._
import org.apache.poi.hssf.usermodel.{HSSFCell, HSSFRow, HSSFSheet, HSSFWorkbook}
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.hssf.extractor.ExcelExtractor
import org.apache.poi.ss.usermodel.{Cell, Row, Sheet}

import collection.JavaConverters._
import scala.collection.IterableView
val inp = new FileInputStream("/Users/warrenronsiek/Projects/CRFSC/src/main/scala/C10001.xls")
val wb = new HSSFWorkbook(new POIFSFileSystem(inp))
val sheet: Iterable[Row] = wb.getSheetAt(0).asScala

case class SheetCell(rowIndex: Int, colIndex: Int, value: Any)


val lst = Iterator(sheet map {row: Row =>
  row.asScala map {cell: Cell => SheetCell(row.getRowNum, cell.getColumnIndex, cell)}
})

//def itrGen(sheet: Iterable[Row]): Iterator[SheetCell] = {
//  for (row: Row <- sheet){
//    for (c: Cell <- row)
//      SheetCell(row.getRowNum, c.getColumnIndex, c)
//  }
//}
//
//val i = itrGen(sheet)
//i.next()
//  r match {
//for (r <- sh) {
//    case r: HSSFRow => System.out.println(r)
//    case _ => Nil
//  }
//}
//val ex = new ExcelExtractor(wb)
//ex.setIncludeBlankCells(true)
//ex.setIncludeCellComments(true)
//ex.setIncludeHeadersFooters(true)
//ex.setFormulasNotResults(false)
//val s = ex.
//
//System.out.println(s)